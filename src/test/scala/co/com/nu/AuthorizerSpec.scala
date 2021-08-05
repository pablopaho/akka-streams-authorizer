package co.com.nu

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, Inside, Inspectors, OptionValues}
import org.scalatestplus.mockito.MockitoSugar

abstract class AuthorizerSpec  extends AnyWordSpec
  with Matchers
  with OptionValues
  with Inside
  with Inspectors
  with MockitoSugar
  with ScalaFutures
  with BeforeAndAfterAll
