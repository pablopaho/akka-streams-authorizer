package co.com.nu.infrastructure.application

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.Supervision.{Resume, Stop}
import akka.stream.{ActorAttributes, ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.util.ByteString
import co.com.nu.infrastructure.Injector
import play.api.libs.json.{JsValue, Json}

import java.nio.file.{Path, Paths}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt

object AuthorizerApplicationStream  extends App {

  val injector = new Injector
  lazy val maxFrameLength = 512

  implicit val actorSystem = ActorSystem("authorizer")
  import actorSystem.dispatcher
  implicit val flowMaterializer = ActorMaterializer()

  val operationsFile: Path = Paths.get("operations")

  val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(operationsFile)

  val decodingFlow: Flow[ByteString, String, NotUsed] = Framing
    .delimiter(ByteString(System.lineSeparator()), maximumFrameLength = maxFrameLength, allowTruncation = true)
    .map(_.utf8String)

  val processOperationFlow: Flow[String, JsValue, NotUsed] = Flow[String].mapAsync(1){ line: String =>
    injector.authorizerController.processOperation(line)
  }

  val sinkPrintln: Sink[Any, Future[Done]] = Sink.foreach(println)

  /** resume = skips faulty element
    stop = stop the stream
    restart = resume + clears  internal state **/
  val processOperationFlowSupervised = processOperationFlow.withAttributes(ActorAttributes.supervisionStrategy {
    case error: RuntimeException =>
      println(Json.obj("account"  -> "{}", "violations" -> s"${error.getMessage}"))
      Resume
    case error: Exception =>
      println(Json.obj("account"  -> "{}", "violations" -> s"${error.getMessage}"))
      Resume
    case _ => Stop
  })

  source
    .via(decodingFlow)
    .via(processOperationFlowSupervised)
    .runWith(sinkPrintln).andThen {
    case _ =>
      actorSystem.terminate()
      Await.ready(actorSystem.whenTerminated, 3.seconds)
  }

}
