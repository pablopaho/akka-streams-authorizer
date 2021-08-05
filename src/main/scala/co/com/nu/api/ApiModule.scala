package co.com.nu.api

import co.com.nu.domain.DomainModule

import scala.concurrent.ExecutionContext

trait ApiModule { this: DomainModule =>
  implicit def executionContext: ExecutionContext
  val authorizerApi: AuthorizerApi = new AuthorizerApi(authorizerRepository)
}
