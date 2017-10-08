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

abstract class BotlinMessageEvent(
        val channelId: String,
        val message: String,
        val rawMessage: String,
        val sender: BotlinMessageSender,
        val session: BotlinMessageSession,
        val reply: (String) -> Unit) : BotlinEvent<Unit> {
    val isMention: Boolean
        get() = message != rawMessage
}

data class BotlinMessageRequest(
        val channelId: String,
        val message: String
)