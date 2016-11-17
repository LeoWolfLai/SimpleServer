package Service.Chat

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.{FlowShape, OverflowStrategy}
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl._


class ChatRoom(roomId: Int, actorSystem: ActorSystem) {

	private[this] val chatRoomActor = actorSystem.actorOf(Props(classOf[ChatRoomActor], roomId))

	def webSocketFlow(user: String): Flow[Message, Message, _]  = {

		val sendBackToUser = Flow[ChatMessage].map {
			case ChatMessage(usr, str) => TextMessage(s"[$usr]: $str")
		}

		val textFromUser = Flow[Message].collect {
			case TextMessage.Strict(txt) => IncomingMessage(user, txt)
		}

		val source = Source.actorRef(10, OverflowStrategy.fail).via(sendBackToUser)
			.mapMaterializedValue(UserJoined(user, _))

		val sink = Sink.actorRef[ChatEvents](chatRoomActor, UserLeft(user))

		Flow.fromGraph(GraphDSL.create() { implicit builder =>

			val out = builder.add(source)

			val in = builder.add(textFromUser)

			in ~> sink

			FlowShape(in.in, out.out)
		})
	}

	def sendMessage(message: ChatMessage): Unit = chatRoomActor ! message

}

object ChatRoom {
	def apply(roomId: Int)(implicit actorSystem: ActorSystem) = new ChatRoom(roomId, actorSystem)
}
