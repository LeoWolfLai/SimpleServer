import Service.Chat.ChatRoomCreator
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

/**
	* Created by leo on 2016/11/14.
	*/
class SimpleServer {

}

object SimpleServer extends App{

	val interface = "127.0.0.1"
	val restPort = 7777
	val wsPort = 8888

	val route:Route =
		get {
			pathPrefix("chat" / IntNumber) { chatRoomId =>
				path(Slash ~ Remaining) { userName =>
					handleWebSocketMessages(ChatRoomCreator.roomJoin(chatRoomId).webSocketFlow(userName))
				}
			}
		}
		EchoService.route ~
		ChatService.route

	/*implicit val m = ActorMaterializer()
	implicit val ec = actorSystem.dispatcher*/

	Http().bindAndHandle(WebSocketAccept(sysRpcHandler, usr), interface, restPort)
	Http().bindAndHandle(WebSocketAccept(sysRpcHandler, usr), interface, wsPort)
	println(s"Server is now online at http://$interface:$wsPort and $restPort\nPress RETURN to stop...")
}
