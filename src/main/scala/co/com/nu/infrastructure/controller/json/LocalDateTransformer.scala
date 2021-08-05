package co.com.nu.infrastructure.controller.json

import play.api.libs.json.{Format, JsResult, JsString, JsSuccess, JsValue}

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, FormatStyle}
import java.util.Date
import java.time.OffsetDateTime

object LocalDateTransformer {
  implicit object DateFormat extends Format[Date] {
    lazy val Format: String = "yyyy-MM-dd'T'HH:mm:ss.SSSX"

    val date: LocalDate = LocalDate.parse("01/01/2020", DateTimeFormatter.ofPattern("MM/dd/yyyy"))
    //val Formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME funciona pero sin time
    val Formatter = DateTimeFormatter.ofPattern(Format)

    def reads(json: JsValue): JsResult[Date] = {
      val dateString: String = json.as[String]
      val odt = OffsetDateTime.parse(dateString)
      val instant = odt.toInstant
      val date: Date = Date.from(instant)

      val res = JsSuccess(Date.from(instant))
      //println(s"formatted date :$res")
      res
    }
    def writes(date: Date): JsString = {
      import java.text.SimpleDateFormat
      import java.util.Locale
      val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH)
      import java.util.TimeZone
      sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"))
      sdf.format(date)
      //println(s"simpleFOrmat date : ${sdf.format(date)}")

      JsString(sdf.format(date))
    }
  }

}
