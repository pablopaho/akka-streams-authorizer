package co.com.nu.api.business.rules

import co.com.nu.api.business.rules.AuthorizerTxBusinessRules.violations
import co.com.nu.domain.account.Account
import co.com.nu.domain.account.Account.{AccountAlreadyInitialized, CreatedAccount, NotExistedAccount}

object AuthorizerAccountBusinessRules {

  lazy val ErrorAccountCantCreate = "error-account-cant-create"
  lazy val AccountAlreadyInitialized = "account-already-initialized"

  def checkAccountBusinessRules(account: Account): violations = {
    account match {
      case _: NotExistedAccount => List(ErrorAccountCantCreate)
      case _: CreatedAccount => Nil
      case _: AccountAlreadyInitialized => List(AccountAlreadyInitialized)
    }
  }

}
