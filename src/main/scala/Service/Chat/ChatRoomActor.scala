package Service.Chat

import akka.actor.{Actor, ActorRef}
import Service.SystemMessage

class ChatRoomActor(roomId: Int) extends Actor {

	var users: Map[String, ActorRef] = Map.empty[String, ActorRef]

	override def receive: Receive = {
		case UserJoined(name, actorRef) =>
			users += name -> actorRef
			broadcast(SystemMessage.chatSystemMessage(s"User $name joined channel..."))
			println(s"User $name joined channel[$roomId]")

		case UserLeft(name) =>
			println(s"User $name left channel[$roomId]")
			broadcast(SystemMessage.chatSystemMessage(s"User $name left channel[$roomId]"))
			users -= name

		case msg: IncomingMessage =>
			broadcast(ChatMessage(msg.sender,msg.message))
	}

	def broadcast(message: ChatMessage): Unit = users.values.foreach(_ ! message)
}
