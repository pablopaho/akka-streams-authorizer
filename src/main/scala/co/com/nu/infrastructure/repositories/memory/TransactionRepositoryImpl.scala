package co.com.nu.infrastructure.repositories.memory

import co.com.nu.domain.transaction.{Amount, Merchant, Time, Transaction, TransactionRepository}
import scala.concurrent.Future

class TransactionRepositoryImpl extends TransactionRepository{
  private var txs: List[Transaction] = Nil

  override def createTx(merchant: Merchant, amount: Amount, time: Time): Future[Transaction] = {
    val tx = Transaction(merchant, amount, time)
    txs = txs ::: List(tx)
    Future.successful(tx)
  }

  override def loadAllTx(): Future[List[Transaction]] = Future.successful(txs)

}
