package co.com.nu.infrastructure.controller.models

import co.com.nu.api.business.rules.AuthorizerTxBusinessRules.violations
import co.com.nu.domain.account.{ActiveCard, AvailableLimit}

final case class AccountDataStdOut(activeCard: Boolean, availableLimit: Int)
final case class AccountStdOut(account: AccountDataStdOut, violations: List[String])

object AccountStdOut {
  def toAccountDataStdOut(activeCard: ActiveCard, availableLimit: AvailableLimit): AccountDataStdOut = {
    AccountDataStdOut(ActiveCard.transformToBoolean(activeCard), availableLimit.value)
  }

  def toAccountStdOut(activeCard: ActiveCard, availableLimit: AvailableLimit, violations: violations): AccountStdOut = {
    AccountStdOut(toAccountDataStdOut(activeCard, availableLimit), violations)
  }
}
