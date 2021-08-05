package co.com.nu.infrastructure

import co.com.nu.api.ApiModule
import co.com.nu.domain.DomainModule

import scala.concurrent.ExecutionContext

class Injector extends DomainModule with ApiModule with InfrastructureModule
