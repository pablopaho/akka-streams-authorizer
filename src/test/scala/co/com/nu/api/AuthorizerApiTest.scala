package co.com.nu.api

import co.com.nu.AuthorizerSpec
import co.com.nu.domain.AuthorizerDomainData
import co.com.nu.domain.account.Account.{AccountAlreadyInitialized, CreatedAccount}
import co.com.nu.domain.account.{AccountRepository, ActiveCard, AvailableLimit}
import co.com.nu.domain.authorizer.{Authorizer, AuthorizerId, AuthorizerRepository}
import co.com.nu.domain.transaction.{Amount, TransactionRepository}
import org.mockito.Mockito.when

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class AuthorizerApiTest extends AuthorizerSpec with AuthorizerDomainData {

  implicit var accountRepositoryMock     : AccountRepository     = _
  implicit var authorizerRepositoryMock  : AuthorizerRepository  = _
  implicit var transactionRepositoryMock : TransactionRepository = _
  implicit val executionContext          : ExecutionContext      = ExecutionContext.Implicits.global
  var authorizerApi                      : AuthorizerApi         = _

  override protected def beforeAll(): Unit = {
    accountRepositoryMock     = mock[AccountRepository]
    authorizerRepositoryMock  = mock[AuthorizerRepository]
    transactionRepositoryMock = mock[TransactionRepository]
    authorizerApi = new AuthorizerApi(authorizerRepositoryMock)
    super.beforeAll()
  }

  "Authorizer API" when {
    "api create one account" should {
      "response empty violations" in {
        //arrange
        when(authorizerRepositoryMock.findOne())
          .thenReturn(Future.successful(Authorizer(authorizerIdOne)(accountRepositoryMock, transactionRepositoryMock)))
        when(accountRepositoryMock.find())
          .thenReturn(Future.successful(CreatedAccount(inActiveCard, availableLimit100)))

        //act
        val responseF = authorizerApi.createAccount(inActiveCard, availableLimit100)
        val activeAddressT = Try(Await.result(responseF, 3.seconds))

        //assert
        activeAddressT match {
          case Success(response) => assert(response._2.isEmpty)
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }

      }
    }

    "api create two accounts" should {
      "response violations" in {
        //arrange
        when(authorizerRepositoryMock.findOne())
          .thenReturn(Future.successful(Authorizer(authorizerIdOne)(accountRepositoryMock, transactionRepositoryMock)))
        when(accountRepositoryMock.find())
          .thenReturn(Future.successful(CreatedAccount(inActiveCard, availableLimit100)))

        //act
        val responseF = authorizerApi.createAccount(inActiveCard, availableLimit100)
        val activeAddressT = Try(Await.result(responseF, 3.seconds))

        //assert
        activeAddressT match {
          case Success(response) => assert(response._2.isEmpty)
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }

        //second account
        when(authorizerRepositoryMock.findOne())
          .thenReturn(Future.successful(Authorizer(authorizerIdTwo)(accountRepositoryMock, transactionRepositoryMock)))
        when(accountRepositoryMock.find())
          .thenReturn(Future.successful(AccountAlreadyInitialized(inActiveCard, availableLimit350)))

        //act
        val responseF2 = authorizerApi.createAccount(inActiveCard, availableLimit350)
        val activeAddressT2 = Try(Await.result(responseF2, 3.seconds))

        //assert
        activeAddressT2 match {
          case Success(response) => assert(response._2 == List("account-already-initialized"))
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }

      }
    }

    "api create an account and a transaction" should {
      "response an account wit update AvailableLimit" in {
        //arrange
        val startAccount = CreatedAccount(activeCard, availableLimit100)
        val createdAccountResponse = CreatedAccount(activeCard, AvailableLimit(availableLimit100.value - transactionBurger.amount.value))
        when(authorizerRepositoryMock.findOne())
          .thenReturn(Future.successful(Authorizer(authorizerIdOne)(accountRepositoryMock, transactionRepositoryMock)))
        when(accountRepositoryMock.find())
          .thenReturn(Future.successful(startAccount))
        when(transactionRepositoryMock.loadAllTx()).thenReturn(Future.successful(Nil))
        when(transactionRepositoryMock.createTx(merchantBurger, amount, time)).thenReturn(Future.successful(transactionBurger))
        when(accountRepositoryMock.setAvailableLimit(AvailableLimit(transactionBurger.amount.value)))
          .thenReturn(Future.successful(createdAccountResponse))

        //act
        val responseF = authorizerApi.createTx(merchantBurger, amount, time)
        val responseT = Try(Await.result(responseF, 3.seconds))

        //assert
        responseT match {
          case Success((account, violations)) =>
            assert(account.asInstanceOf[CreatedAccount] == createdAccountResponse)
            assert(violations.isEmpty)
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }
      }
    }

    "api create a transaction with Inactive account" should {
      "response card-not-active" in {
        //arrange
        val startAccount = CreatedAccount(inActiveCard, availableLimit100)
        val createdAccountResponse = CreatedAccount(inActiveCard, AvailableLimit(availableLimit100.value - transactionBurger.amount.value))
        when(authorizerRepositoryMock.findOne())
          .thenReturn(Future.successful(Authorizer(authorizerIdOne)(accountRepositoryMock, transactionRepositoryMock)))
        when(accountRepositoryMock.find())
          .thenReturn(Future.successful(startAccount))
        when(transactionRepositoryMock.loadAllTx()).thenReturn(Future.successful(Nil))
        when(transactionRepositoryMock.createTx(merchantBurger, amount, time)).thenReturn(Future.successful(transactionBurger))
        when(accountRepositoryMock.setAvailableLimit(AvailableLimit(transactionBurger.amount.value)))
          .thenReturn(Future.successful(createdAccountResponse))

        //act
        val responseF = authorizerApi.createTx(merchantBurger, amount, time)
        val responseT = Try(Await.result(responseF, 3.seconds))

        //assert
        responseT match {
          case Success((account, violations)) =>
            assert(account.asInstanceOf[CreatedAccount] == startAccount)
            assert(violations == List("card-not-active"))
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }
      }
    }

    "api create a transaction that exceed available amount account" should {
      "response insufficient-limit" in {
        //arrange
        val transactionBurgerExceed = transactionBurger.copy(amount = Amount(120))
        val startAccount = CreatedAccount(activeCard, availableLimit100)
        val createdAccountResponse = CreatedAccount(activeCard, AvailableLimit(availableLimit100.value - transactionBurgerExceed.amount.value))

        when(authorizerRepositoryMock.findOne())
          .thenReturn(Future.successful(Authorizer(authorizerIdOne)(accountRepositoryMock, transactionRepositoryMock)))
        when(accountRepositoryMock.find())
          .thenReturn(Future.successful(startAccount))
        when(transactionRepositoryMock.loadAllTx()).thenReturn(Future.successful(Nil))
        when(transactionRepositoryMock.createTx(transactionBurgerExceed.merchant, transactionBurgerExceed.amount, transactionBurgerExceed.time))
          .thenReturn(Future.successful(transactionBurgerExceed))
        when(accountRepositoryMock.setAvailableLimit(AvailableLimit(transactionBurger.amount.value)))
          .thenReturn(Future.successful(createdAccountResponse))

        //act
        val responseF = authorizerApi.createTx(transactionBurgerExceed.merchant, transactionBurgerExceed.amount, transactionBurgerExceed.time)
        val responseT = Try(Await.result(responseF, 3.seconds))

        //assert
        responseT match {
          case Success((account, violations)) =>
            assert(account.asInstanceOf[CreatedAccount] == startAccount)
            assert(violations == List("insufficient-limit"))
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }
      }
    }
  }


}
