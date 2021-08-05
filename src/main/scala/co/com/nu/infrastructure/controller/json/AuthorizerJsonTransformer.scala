package co.com.nu.infrastructure.controller.json

import co.com.nu.infrastructure.controller.models._
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsResult, JsValue, Json, __}
import co.com.nu.infrastructure.controller.json.LocalDateTransformer.DateFormat
import java.util.Date

object AuthorizerJsonTransformer {

  implicit val AccountDataStdinFormat: Format[AccountDataStdin] = (
    (__ \ "active-card").format[Boolean] ~
      (__ \ "available-limit").format[Int]
    )(AccountDataStdin.apply _, unlift(AccountDataStdin.unapply))

  implicit val AccountStdInFormat = Json.format[AccountStdIn]
  implicit val AccountDataStdOutFormat: Format[AccountDataStdOut] = (
    (__ \ "active-card").format[Boolean] ~
      (__ \ "available-limit").format[Int]
    )(AccountDataStdOut.apply _, unlift(AccountDataStdOut.unapply))
  implicit val AccountStdOutFormat = Json.format[AccountStdOut]

  implicit val TransactionDataStdInFormat: Format[TransactionDataStdIn] = (
    (__ \ "merchant").format[String] ~
    (__ \ "amount").format[Int] ~
    (__ \ "time").format[Date](DateFormat)
  )(TransactionDataStdIn.apply, unlift(TransactionDataStdIn.unapply))
  implicit val TransactionStdInFormat = Json.format[TransactionStdIn]

  def transformJsonToModels(line: String): JsResult[TxStdIn] = {
    val lineParsed: JsValue = Json.parse(line)
    (lineParsed \ "transaction").result.toOption match {
      case Some(transaction) =>
        Json.fromJson(lineParsed)(TransactionStdInFormat)
      case None =>
        Json.fromJson(lineParsed)(AccountStdInFormat)
    }
  }

}
