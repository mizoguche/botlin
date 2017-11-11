package info.mizoguche.botlin.engine

import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.SlackUser
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.BotMessageSender
import info.mizoguche.botlin.BotMessageSession
import info.mizoguche.botlin.Pipelines
import kotlinx.coroutines.experimental.launch

class BotSlackMessageSender(private val user: SlackUser) : BotMessageSender {
    override val senderId: String
        get() = user.id
    override val senderUserName: String
        get() = user.userName
    override val senderDisplayName: String
        get() = user.realName
}

class BotSlackSession(private val slackSession: SlackSession) : BotMessageSession {
    override val mentionChar: String
        get() = "@"
    override val userName: String
        get() = slackSession.sessionPersona().userName
}

class BotSlackMessage(private val slackSession: SlackSession, private val event: SlackMessagePosted) : BotMessage {
    private val messageSender = BotSlackMessageSender(event.sender)
    private val botSession = BotSlackSession(slackSession)

    override val channelId: String
        get() = event.channel.id
    override val message: String
        get() = event.messageContent.replace("<@${slackSession.sessionPersona().id}> ", "")
    override val rawMessage: String
        get() = event.messageContent
    override val sender: BotMessageSender
        get() = messageSender
    override val session: BotMessageSession
        get() = botSession

    override fun reply(body: String) {
        slackSession.sendMessage(event.channel, body)
    }
}

class SlackEngine(val configuration: Configuration) : BotEngine {
    private var slackSession: SlackSession? = null

    suspend override fun start(pipelines: Pipelines) {
        val session = SlackSessionFactory.createWebSocketSlackSession(configuration.token)
        slackSession = session
        session.connect()
        session.addMessagePostedListener { event, sess ->
            if (event.sender.id != session.sessionPersona().id) {
                launch { pipelines[BotMessage::class].execute(BotSlackMessage(sess, event)) }
            }
        }
    }

    override fun stop() {
        slackSession?.disconnect()
    }

    class Configuration {
        var token = ""
    }

    companion object Factory : BotEngineFactory<Configuration> {
        override fun create(configure: Configuration.() -> Unit): BotEngine {
            val conf = Configuration().apply(configure)
            return SlackEngine(conf)
        }
    }
}