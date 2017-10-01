package info.mizoguche.botlin

data class BotlinMessageSender(
        val senderId: String,
        val senderUserName: String,
        val senderDisplayName: String
)

data class BotlinMessageSession(
        val mentionChar: String,
        val botUsername: String
) {
    val mentionPrefix
        get() = "$mentionChar$botUsername"
}

data class BotlinMessageEvent(
        val message: String,
        val rawMessage: String,
        val sender: BotlinMessageSender,
        val session: BotlinMessageSession,
        val reply: (String) -> Unit) {
    val isMention: Boolean
        get() = message != rawMessage
}
