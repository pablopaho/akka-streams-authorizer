package co.com.nu.domain.account

import co.com.nu.domain.account.Account.CreatedAccount
import scala.concurrent.Future

trait AccountRepository {
  def find(): Future[Account]
  def create(activeCard: ActiveCard, availableLimit: AvailableLimit): Future[CreatedAccount]
  def setAvailableLimit(spent: AvailableLimit): Future[Account]
}
