package co.com.nu.api.business.rules

import co.com.nu.domain.account.Account.{AccountAlreadyInitialized, CreatedAccount, NotExistedAccount}
import co.com.nu.domain.account.ActiveCard.{Active, Inactive}
import co.com.nu.domain.account.{Account, ActiveCard, AvailableLimit}
import co.com.nu.domain.transaction.{Amount, Time, Transaction}

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

object AuthorizerTxBusinessRules {

  type violations = List[String]
  lazy val AccountNotInitialized = "account-not-initialized"
  lazy val CardNotActive = "card-not-active"
  lazy val InsufficientLimit = "insufficient-limit"
  lazy val HighFrequencySmallInterval = "high-frequency-small-interval"
  lazy val MinInterval = 120

  def checkBusinessRulesTx(account: Account, txHistoric: List[Transaction], newAmount: Amount, newTime: Time)
                          (implicit ec: ExecutionContext): Future[violations] = {
    Future(account match {
      case NotExistedAccount() =>
        List(AccountNotInitialized)
      case AccountAlreadyInitialized(activeCard, availableLimit) =>
        collectValidations(txHistoric, newAmount, newTime, activeCard, availableLimit)
      case CreatedAccount(activeCard, availableLimit) =>
        collectValidations(txHistoric, newAmount, newTime, activeCard, availableLimit)
    })
  }

  private def collectValidations(txHistoric: List[Transaction],
                                 newAmount: Amount,
                                 newTime: Time,
                                 activeCard: ActiveCard,
                                 availableLimit: AvailableLimit): List[String] = {
    checkAccountStatus(activeCard) :::
    checkAvailableLimitFixed(newAmount, txHistoric, availableLimit) :::
    checkHighFrequencyInterval(txHistoric, newTime)
  }

  private def checkAccountStatus(activeCard: ActiveCard): violations = {
    activeCard match {
      case Active => Nil
      case Inactive => List(CardNotActive)
    }
  }

  private def checkAvailableLimitFixed(amount: Amount, txHistoric: List[Transaction], availableLimit: AvailableLimit): violations = {
    val availableAmount = if (txHistoric.isEmpty) {
                            availableLimit.value - amount.value
    } else {
                            txHistoric.map(_.amount.value).sum - amount.value
    }

    if (availableAmount < 0) {
      List(InsufficientLimit)
    } else {
      Nil
    }
  }

  private def checkHighFrequencyInterval(txHistoric: List[Transaction], newTime: Time): violations = {
    if (txHistoric.isEmpty){
      Nil
    }else{
      val (timeBase, timeInterval) = makeInterval(txHistoric.head.time, MinInterval)
      val countTxsBetweenInterval = txHistoric.count(tx =>
        (tx.time.date.toInstant.isAfter(timeBase.minusSeconds(1))) &&
          (tx.time.date.toInstant.isBefore(timeInterval)))
      val checkIfNewTxIsInInterval =
        newTime.date.toInstant.isAfter(timeBase.minusSeconds(1)) &&
          newTime.date.toInstant.isBefore(timeInterval)
      if (checkIfNewTxIsInInterval && (countTxsBetweenInterval + 1 > 3)) {
        List(HighFrequencySmallInterval)
      } else {
        Nil
      }
    }
  }

  private def makeInterval(time: Time, seconds: Long): (Instant, Instant) = {
    val timeBase = time.date.toInstant
    val timeInterval = timeBase.plusSeconds(seconds)
    (timeBase, timeInterval)
  }

}
