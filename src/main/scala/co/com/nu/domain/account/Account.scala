package co.com.nu.domain.account

sealed trait Account

case class AvailableLimit(value: Int)

object Account {

  final case class NotExistedAccount() extends Account

  final case class AccountAlreadyInitialized(activeCard: ActiveCard, availableLimit: AvailableLimit) extends Account

  final case class CreatedAccount(activeCard: ActiveCard, availableLimit: AvailableLimit) extends Account

}
