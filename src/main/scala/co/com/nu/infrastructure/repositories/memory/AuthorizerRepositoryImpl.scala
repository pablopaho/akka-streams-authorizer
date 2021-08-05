package co.com.nu.infrastructure.repositories.memory

import co.com.nu.domain.account.AccountRepository
import co.com.nu.domain.authorizer.{Authorizer, AuthorizerId, AuthorizerRepository}
import co.com.nu.domain.transaction.TransactionRepository

import scala.concurrent.{ExecutionContext, Future}

class AuthorizerRepositoryImpl(accountRepository: AccountRepository, transactionRepository: TransactionRepository)
                              (implicit ec: ExecutionContext) extends AuthorizerRepository {
  override def findOne(): Future[Authorizer] = {
    Future.successful(Authorizer(AuthorizerId(1))(accountRepository, transactionRepository))
  }
}
