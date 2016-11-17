package Service.Chat

import akka.actor.ActorRef

sealed trait ChatEvents

case class UserJoined(name: String, userActor: ActorRef) extends ChatEvents

case class UserLeft(name: String) extends ChatEvents

case class IncomingMessage(sender: String, message: String) extends ChatEvents

case class ChatMessage(sender: String, text: String)

