package co.com.nu.api

import co.com.nu.api.business.rules.AuthorizerAccountBusinessRules.checkAccountBusinessRules
import co.com.nu.api.business.rules.AuthorizerTxBusinessRules.{checkBusinessRulesTx, violations}
import co.com.nu.domain.account.{Account, ActiveCard, AvailableLimit}
import co.com.nu.domain.authorizer.{Authorizer, AuthorizerRepository}
import co.com.nu.domain.transaction.{Amount, Merchant, Time, Transaction}

import scala.concurrent.{ExecutionContext, Future}

class AuthorizerApi(authorizerRepository: AuthorizerRepository)(implicit ec: ExecutionContext) {

  def loadAccount(authorizer: Authorizer): Future[Account] = {
    authorizer.loadAccount()
  }

  def createAccount(activeCard: ActiveCard, availableLimit: AvailableLimit): Future[(Account, violations)] = {
    for {
      authorizer <- authorizerRepository.findOne()
      account    <- authorizer.createAccount(activeCard, availableLimit)
      violations =  checkAccountBusinessRules(account)
    } yield (account, violations)
  }

  def loadAllTx(authorizer: Authorizer): Future[List[Transaction]] = {
    authorizer.loadAllTx()
  }

  def createTx(merchant: Merchant, newAmount: Amount, newTime: Time): Future[(Account, violations)] = {
    (for {
      authorizer <- authorizerRepository.findOne()
      account    <- loadAccount(authorizer)
      txHistoric <- loadAllTx(authorizer)
      violations <- checkBusinessRulesTx(account, txHistoric, newAmount, newTime)
      resultTx   =  processTx(merchant, newAmount, newTime, authorizer, account, violations)
    } yield resultTx).flatMap(transactionProcessed => transactionProcessed)
  }

  private def processTx(merchant: Merchant,
                        amount: Amount,
                        time: Time,
                        authorizer: Authorizer,
                        account: Account,
                        violations: violations): Future[(Account, violations)] = {
    if (violations.isEmpty) {
      authorizer.createTx(merchant, amount, time).map(account => (account, violations))
    } else {
      Future.successful((account, violations))
    }
  }

}
