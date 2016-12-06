import Node.api._
import Service.Chat.ChatRoomCreator
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, RequestContext, Route, RouteResult}
import akka.stream.ActorMaterializer

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

/**
	* Created by leo on 2016/11/14.
	*/
class SimpleServer (implicit system: ActorSystem, mat: ActorMaterializer, ec:ExecutionContext) {
	def toStrict(timeout: FiniteDuration): Directive[Unit] = {
		def toStrict0(inner: Unit ⇒ Route): Route = {
			val result: RequestContext ⇒ Future[RouteResult] = c ⇒ {
				c.request.entity.toStrict(timeout).flatMap { strict ⇒
					val c1 = c.withRequest(c.request.withEntity(strict))
					inner()(c1)
				}
			}
			result
		}
		Directive[Unit](toStrict0)
	}

	val route:Route = {
		get {
			path("chat" / IntNumber / Segment) { (chatRoomId, userName) =>
				handleWebSocketMessages(ChatRoomCreator.roomJoin(chatRoomId).webSocketFlow(userName))
			}
		} ~
		post {
			path("user") {
				entity(as[UserApi]) { userapi =>
					complete {
						s"api is [${userapi.api}], parameter is [${userapi.param}]"
					}
				}
			} ~ path("user" / "upload") {
				toStrict(300.second) {
					formFields('json.as[String]) { json =>
						uploadedFile("file") {
							case (meta, file) =>
								val jsonStr = java.net.URLDecoder.decode(json, "UTF-8")
								file.delete()
								complete {
									s"got file! file name is： [${meta.getFileName}]"
								}
						}
					}
				}
			}
		}
	}

	Http().bindAndHandle(route, SimpleServer.interface, SimpleServer.restPort)
	Http().bindAndHandle(route , SimpleServer.interface, SimpleServer.wsPort)
	println(s"Server is now online at http://${SimpleServer.interface}:${SimpleServer.restPort} and http://${SimpleServer.interface}:${SimpleServer.wsPort}\n")

}

object SimpleServer extends App{

	val interface = "127.0.0.1"
	val restPort = 7777
	val wsPort = 8888

	implicit val system = ActorSystem()
	implicit val materializer = ActorMaterializer()
	implicit val ec = system.dispatcher

	sys.addShutdownHook{
		val terminatefuture = system.terminate()
		Await.result(terminatefuture, 5.seconds)
	}

	new SimpleServer
}
