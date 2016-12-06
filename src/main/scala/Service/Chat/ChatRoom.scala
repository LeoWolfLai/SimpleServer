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

		val sink = Sink.actorRef[ChatEvents](chatRoomActor, UserLeft(user))

		Flow.fromGraph(GraphDSL.create(Source.actorRef[ChatMessage](10, OverflowStrategy.fail)) { implicit builder =>
			userActor =>
				val merge = builder.add(Merge[ChatEvents](2))
				val source = builder.materializedValue.map(UserJoined(user, _))
				val in = builder.add(textFromUser)
				val out = builder.add(sendBackToUser)

				in ~> merge.in(0)
				source ~> merge.in(1)

				merge ~> sink

				userActor ~> out

				FlowShape(in.in, out.out)
		})
	}

	def sendMessage(message: ChatMessage): Unit = chatRoomActor ! message

}

object ChatRoom {
	def apply(roomId: Int)(implicit actorSystem: ActorSystem) = new ChatRoom(roomId, actorSystem)
}
