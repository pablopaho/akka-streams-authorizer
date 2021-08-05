package co.com.nu.infrastructure.controller

import co.com.nu.api.AuthorizerApi
import co.com.nu.api.business.rules.AuthorizerTxBusinessRules.violations
import co.com.nu.domain.account.Account.{AccountAlreadyInitialized, CreatedAccount}
import co.com.nu.domain.account.{Account, ActiveCard, AvailableLimit}
import co.com.nu.domain.transaction.{Amount, Merchant, Time}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import co.com.nu.infrastructure.controller.json.AuthorizerJsonTransformer._
import co.com.nu.infrastructure.controller.models.AccountStdOut.toAccountStdOut
import co.com.nu.infrastructure.controller.models.{AccountDataStdOut, AccountStdIn, AccountStdOut, TransactionStdIn, TxStdIn}

import scala.concurrent.{ExecutionContext, Future}

class AuthorizerController(authorizerApi: AuthorizerApi)(implicit ec: ExecutionContext) {

  def processOperation(line: String): Future[JsValue] = {
    transformJsonToModels(line) match {
      case JsSuccess(txStdIn: TxStdIn, path) =>
        txStdIn match {
          case accountStdIn: AccountStdIn => createAccount(accountStdIn)
          case transactionStdIn: TransactionStdIn => createTx(transactionStdIn)
        }
      case JsError(errors) =>
        Future.failed(new Exception(s"Json parsed error : $errors"))
      }
    }

  private def createAccount(accountStdIn: AccountStdIn): Future[JsValue] = {
    authorizerApi.createAccount(
      ActiveCard(accountStdIn.account.activeCard),
      AvailableLimit(accountStdIn.account.availableLimit)).map{ case (account, violations) =>
      account match {
        case accountCreated: CreatedAccount =>
          Json.toJson(toAccountStdOut(accountCreated.activeCard, accountCreated.availableLimit, violations))
        case _ =>
          Json.toJson(toAccountStdOut(
            ActiveCard(accountStdIn.account.activeCard),
            AvailableLimit(accountStdIn.account.availableLimit),
            violations))
      }
    }
  }

  private def createTx(transactionStdIn: TransactionStdIn): Future[JsValue] = {
    authorizerApi.createTx(
      Merchant(transactionStdIn.transaction.merchant),
      Amount(transactionStdIn.transaction.amount),
      Time(transactionStdIn.transaction.time)
    ).map { case (account: Account, violations) =>
      account match {
        case accountCreated: CreatedAccount =>
          Json.toJson(toAccountStdOut(accountCreated.activeCard, accountCreated.availableLimit, violations))
        case accountAlreadyInitialized: AccountAlreadyInitialized =>
          Json.toJson(toAccountStdOut(accountAlreadyInitialized.activeCard, accountAlreadyInitialized.availableLimit, violations))
        case _ =>
          Json.obj("account"  -> "{}", "violations" -> violations)
      }
    }
  }

}
