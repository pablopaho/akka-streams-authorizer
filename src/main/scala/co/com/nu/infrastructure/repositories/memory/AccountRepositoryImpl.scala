package co.com.nu.infrastructure.repositories.memory

import co.com.nu.domain.account.Account.{AccountAlreadyInitialized, CreatedAccount, NotExistedAccount}
import co.com.nu.domain.account.{Account, AccountRepository, ActiveCard, AvailableLimit}
import co.com.nu.domain.transaction.Amount

import scala.concurrent.{ExecutionContext, Future}

class AccountRepositoryImpl(implicit ec: ExecutionContext) extends AccountRepository {

  private var accountCreated: Account = NotExistedAccount()

  private def saveAccount(activeCard: ActiveCard, availableLimit: AvailableLimit): CreatedAccount = {
    val account = CreatedAccount(activeCard, availableLimit)
    accountCreated = account
    account
  }

  private def loadAccount(): Account = {
    accountCreated match {
      case accountCreated: CreatedAccount =>
        AccountAlreadyInitialized(accountCreated.activeCard, accountCreated.availableLimit)
      case account: Account => account
    }
  }

  override def find(): Future[Account] = Future.successful(loadAccount())

  override def create(activeCard: ActiveCard, availableLimit: AvailableLimit): Future[Account.CreatedAccount] =
    Future.successful(saveAccount(activeCard, availableLimit))

  override def setAvailableLimit(spent: AvailableLimit): Future[Account] = {
    accountCreated = accountCreated match {
      case accountCreated: CreatedAccount =>
        accountCreated.copy(availableLimit = calculateAvailableLimit(spent, accountCreated.availableLimit))
      case accountAlreadyInitialized: AccountAlreadyInitialized =>
        accountAlreadyInitialized.copy(availableLimit =  calculateAvailableLimit(spent, accountAlreadyInitialized.availableLimit))
      case notExistedAccount: NotExistedAccount => notExistedAccount
    }
    Future.successful(accountCreated)
  }

  private def calculateAvailableLimit(spent: AvailableLimit, availableLimit: AvailableLimit): AvailableLimit = {
    AvailableLimit(availableLimit.value - spent.value)
  }
}
