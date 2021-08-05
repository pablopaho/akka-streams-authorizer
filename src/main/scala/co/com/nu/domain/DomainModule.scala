package co.com.nu.domain

import co.com.nu.domain.account.AccountRepository
import co.com.nu.domain.authorizer.AuthorizerRepository
import co.com.nu.domain.transaction.TransactionRepository

trait DomainModule {
  def accountRepository: AccountRepository
  def authorizerRepository: AuthorizerRepository
  def transactionRepository: TransactionRepository
}
