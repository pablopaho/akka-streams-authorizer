package co.com.nu.infrastructure.controller.models

import java.util.Date

sealed trait TxStdIn

final case class AccountDataStdin(activeCard: Boolean, availableLimit: Int)
final case class AccountStdIn(account: AccountDataStdin) extends TxStdIn

final case class TransactionDataStdIn(merchant: String, amount: Int, time: Date)
final case class TransactionStdIn(transaction: TransactionDataStdIn) extends TxStdIn
