package co.com.nu.domain.transaction

import scala.concurrent.Future

trait TransactionRepository {

  def createTx(merchant: Merchant, amount: Amount, time: Time): Future[Transaction]

  def loadAllTx(): Future[List[Transaction]]

}
