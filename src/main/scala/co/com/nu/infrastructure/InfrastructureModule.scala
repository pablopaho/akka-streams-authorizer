package co.com.nu.infrastructure

import co.com.nu.api.ApiModule
import co.com.nu.domain.account.AccountRepository
import co.com.nu.domain.authorizer.AuthorizerRepository
import co.com.nu.domain.DomainModule
import co.com.nu.domain.transaction.TransactionRepository
import co.com.nu.infrastructure.controller.AuthorizerController
import co.com.nu.infrastructure.repositories.memory.{AccountRepositoryImpl, AuthorizerRepositoryImpl, TransactionRepositoryImpl}

import scala.concurrent.ExecutionContext

trait InfrastructureModule { this: ApiModule with DomainModule =>
  override def accountRepository: AccountRepository = new AccountRepositoryImpl()

  override def transactionRepository: TransactionRepository = new TransactionRepositoryImpl()

  override def authorizerRepository: AuthorizerRepository = new AuthorizerRepositoryImpl(accountRepository, transactionRepository)


  override implicit def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global

  val authorizerController: AuthorizerController = new AuthorizerController(authorizerApi)

}
