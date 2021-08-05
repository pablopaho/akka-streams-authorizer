package co.com.nu.domain.authorizer

import scala.concurrent.Future

trait AuthorizerRepository {
  def findOne(): Future[Authorizer]
}
