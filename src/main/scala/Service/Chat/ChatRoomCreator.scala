package Service.Chat

import akka.actor.ActorSystem

object ChatRoomCreator {
	var chatRooms: Map[Int, ChatRoom] = Map.empty[Int, ChatRoom]

	def roomJoin(roomNumber: Int)(implicit actorSystem: ActorSystem): ChatRoom = chatRooms.getOrElse(roomNumber, createChatRoom(roomNumber))

	private def createChatRoom(roomNumber: Int)(implicit actorSystem: ActorSystem): ChatRoom = {
		val chatroom = ChatRoom(roomNumber)
		chatRooms += roomNumber -> chatroom
		chatroom
	}
}
