package co.com.nu.infrastructure

import co.com.nu.AuthorizerSpec
import play.api.libs.json.JsValue

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

class AuthorizerControllerTest extends AuthorizerSpec{

  "AuthorizerControllerTest" when{
    "create one account" should {
      "return tx without violations" in {
        //arrange
        val injector = new Injector
        val createAccount = "{\"account\": {\"active-card\": true, \"available-limit\": 175}}"

        //act
        val responseF = injector.authorizerController.processOperation(createAccount)
        val response = Try(Await.result(responseF, 3.seconds))

        //assert
        response match {
          case Success(value: JsValue) =>
            assert(!value.result.isEmpty)
            assert(value.result.get("violations").toString() == "[]")
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }

      }
    }

    "create two account" should {
      "return tx with violations" in {
        //arrange
        val injector = new Injector


        val createAccount = "{\"account\": {\"active-card\": true, \"available-limit\": 175}}"
        val createAccount2 = "{\"account\": {\"active-card\": true, \"available-limit\": 350}}"

        //act
        val responseF = injector.authorizerController.processOperation(createAccount)
        val response = Try(Await.result(responseF, 3.seconds))

        //assert
        response match {
          case Success(value: JsValue) =>
            assert(!value.result.isEmpty)
            assert(value.result.get("violations").toString() == "[]")
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }

        //second
        val responseF2 = injector.authorizerController.processOperation(createAccount2)
        val response2 = Try(Await.result(responseF2, 3.seconds))

        response2 match {
          case Success(value: JsValue) =>
            assert(!value.result.isEmpty)
            assert(value.result.get("violations").toString() == "[\"account-already-initialized\"]")
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }

      }
    }
  }

}
