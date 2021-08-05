package co.com.nu.domain.authorizer

import co.com.nu.domain._
import co.com.nu.domain.account.Account.{AccountAlreadyInitialized, CreatedAccount, NotExistedAccount}
import co.com.nu.domain.account.{Account, AccountRepository, ActiveCard, AvailableLimit}
import co.com.nu.domain.transaction.{Amount, Merchant, Time, Transaction, TransactionRepository}

import scala.concurrent.{ExecutionContext, Future}


final case class AuthorizerId(value: Int) extends AnyVal

final case class Authorizer(id: AuthorizerId)
                           (accountRepository: AccountRepository, transactionRepository: TransactionRepository)
                           (implicit ec: ExecutionContext) {

  def loadAccount(): Future[Account] = accountRepository.find()

  def createAccount(activeCard: ActiveCard, availableLimit: AvailableLimit): Future[Account] = {
    accountRepository.find().flatMap {
      case _: NotExistedAccount => accountRepository.create(activeCard, availableLimit)
      case createdAccount: CreatedAccount => Future.successful(createdAccount)
      case accountAlreadyInitialized: AccountAlreadyInitialized => Future.successful(accountAlreadyInitialized)
    }
  }

  def createTx(merchant: Merchant, amount: Amount, time: Time): Future[Account] = {
    transactionRepository.createTx(merchant, amount, time).flatMap{ tx =>
      accountRepository.setAvailableLimit(AvailableLimit(amount.value))
    }
  }

  def loadAllTx(): Future[List[Transaction]] = transactionRepository.loadAllTx()

}
