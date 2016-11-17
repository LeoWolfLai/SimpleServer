package Service

import Service.Chat.ChatMessage

object SystemMessage {
	def chatSystemMessage(text: String) = ChatMessage("System", text)
}
