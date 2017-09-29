package info.mizoguche.botlin

data class BotlinMessageSender(
        val senderId: String,
        val senderUserName: String,
        val senderDisplayName: String
)

data class BotlinMessageEvent(
        val message: String,
        val rawMessage: String,
        val sender: BotlinMessageSender,
        val reply: (String) -> Unit) {
    val isMention: Boolean
        get() = message != rawMessage
}
