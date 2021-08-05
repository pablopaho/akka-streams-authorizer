package co.com.nu.domain

import co.com.nu.AuthorizerSpec
import co.com.nu.domain.account.{Account, AccountRepository, ActiveCard, AvailableLimit}
import co.com.nu.domain.account.Account.{AccountAlreadyInitialized, CreatedAccount, NotExistedAccount}
import co.com.nu.domain.authorizer.{Authorizer, AuthorizerId}
import co.com.nu.domain.transaction.TransactionRepository
import org.mockito.Mockito.when

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.concurrent.duration._

class AuthorizerTest extends AuthorizerSpec with AuthorizerDomainData {

  implicit var accountRepositoryMock: AccountRepository = _
  implicit var transactionRepositoryMock: TransactionRepository = _
  implicit val executionContext = ExecutionContext.Implicits.global

  override protected def beforeAll(): Unit = {
    accountRepositoryMock = mock[AccountRepository]
    transactionRepositoryMock = mock[TransactionRepository]
    super.beforeAll()
  }

  "Authorizer" when {
    "create one account" should {
      "return account created" in {
        //arrange
        when(accountRepositoryMock.find()).thenReturn(Future.successful(CreatedAccount(inActiveCard, availableLimit100)))

        //act
        val accountF = Authorizer(authorizerIdOne)(accountRepositoryMock, transactionRepositoryMock)
                      .createAccount(inActiveCard, availableLimit100)
        val accountT = Try(Await.result(accountF, 3.seconds))

        //assert
        accountT match {
          case Success(account: Account) => assert(account.asInstanceOf[CreatedAccount].activeCard == inActiveCard)
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }
      }
    }

    "create one account but the account is already created" should {
      "return account created" in {
        //arrange
        when(accountRepositoryMock.find()).thenReturn(Future.successful(AccountAlreadyInitialized(inActiveCard, availableLimit100)))

        //act
        val accountF = Authorizer(authorizerIdOne)(accountRepositoryMock, transactionRepositoryMock)
                      .createAccount(inActiveCard, availableLimit100)
        val accountT = Try(Await.result(accountF, 3.seconds))

        //assert
        accountT match {
          case Success(account: Account) => assert(account.asInstanceOf[AccountAlreadyInitialized].activeCard == inActiveCard)
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }
      }
    }

    "load an account without account" should{
      "return a createdAccount" in {
        //arrange
        when(accountRepositoryMock.find()).thenReturn(Future.successful(NotExistedAccount()))

        //act
        val accountF = Authorizer(authorizerIdOne)(accountRepositoryMock, transactionRepositoryMock).loadAccount()
        val accountT = Try(Await.result(accountF, 3.seconds))

        //assert
        accountT match {
          case Success(account) => assert(account.asInstanceOf[NotExistedAccount] == NotExistedAccount() )
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }
      }
    }

    "load an created account" should{
      "return a createdAccount" in {
        //arrange
        when(accountRepositoryMock.find()).thenReturn(Future.successful(CreatedAccount(inActiveCard, availableLimit100)))

        //act
        val accountF = Authorizer(authorizerIdOne)(accountRepositoryMock, transactionRepositoryMock).loadAccount()
        val accountT = Try(Await.result(accountF, 3.seconds))

        //assert
        accountT match {
          case Success(account) => assert(account.asInstanceOf[CreatedAccount] == CreatedAccount(inActiveCard, availableLimit100) )
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }
      }
    }

    "create successful tx" should {
      "return an  updated CreatedAccount" in {
        //arrange
        when(transactionRepositoryMock.createTx(merchantBurger, amount, time)).thenReturn(Future.successful(transactionBurger))
        when(accountRepositoryMock.setAvailableLimit(AvailableLimit(transactionBurger.amount.value)))
          .thenReturn(Future.successful(CreatedAccount(inActiveCard, AvailableLimit(availableLimit100.value - transactionBurger.amount.value))))

        //act
        val accountF = Authorizer(authorizerIdOne)(accountRepositoryMock, transactionRepositoryMock).createTx(merchantBurger, amount, time)
        val accountT = Try(Await.result(accountF, 3.seconds))

        //assert
        accountT match {
          case Success(account) => assert(account.asInstanceOf[CreatedAccount].availableLimit == AvailableLimit(availableLimit100.value - transactionBurger.amount.value))
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }
      }
    }

    "load all transactions" should {
      "return a list of transactions" in {
        //arrange
        when(transactionRepositoryMock.loadAllTx()).thenReturn(Future.successful(List(transactionBurger, transactionPizza)))

        //act
        val txHistoricF = Authorizer(authorizerIdOne)(accountRepositoryMock, transactionRepositoryMock).loadAllTx()
        val txHistoricT   = Try(Await.result(txHistoricF, 3.seconds))

        //assert
        txHistoricT match {
          case Success(tx) => assert(tx.length == 2)
          case Failure(exception) => assert(false, s"error ${exception.getMessage}")
        }
      }
    }
  }

}
