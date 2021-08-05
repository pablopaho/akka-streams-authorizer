package co.com.nu.domain

import co.com.nu.domain.account.{ActiveCard, AvailableLimit}
import co.com.nu.domain.authorizer.AuthorizerId
import co.com.nu.domain.transaction.{Amount, Merchant, Time, Transaction}

import java.util.Date

trait AuthorizerDomainData {

  val authorizerIdOne   = AuthorizerId(1)
  val authorizerIdTwo   = AuthorizerId(1)
  val inActiveCard      = ActiveCard.Inactive
  val activeCard        = ActiveCard.Active
  val availableLimit100 = AvailableLimit(100)
  val availableLimit350 = AvailableLimit(350)
  val merchantBurger    = Merchant("Burger")
  val merchantPizza     = Merchant("Pizza")
  val amount            = Amount(20)
  val time              = Time(new Date(2020,10,1))

  val transactionBurger = Transaction(merchantBurger, amount, time)
  val transactionPizza  = Transaction(merchantPizza, amount, time)

}
