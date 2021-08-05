package co.com.nu.domain.transaction

import java.util.Date

final case class Merchant(name: String)
final case class Amount(value: Int)
final case class Time(date: Date)

final case class Transaction(merchant: Merchant, amount: Amount, time: Time)
