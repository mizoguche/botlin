package info.mizoguche.botlin.engine

import com.ullink.slack.simpleslackapi.SlackPreparedMessage
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.SlackUser
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.BotMessageRequest
import info.mizoguche.botlin.BotMessageSender
import info.mizoguche.botlin.BotMessageSession
import info.mizoguche.botlin.BotPipelines
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

class BotSlackMessage(override val engineId: BotEngineId, private val slackSession: SlackSession, private val event: SlackMessagePosted) : BotMessage {
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
        slackSession.sendMessage(event.channel, createSlackMessage(body))
    }
}

private fun createSlackMessage(message: String) = SlackPreparedMessage.Builder()
        .withLinkNames(true)
        .withMessage(message)
        .build()

class SlackEngine(configuration: Configuration) : BotEngine {
    override val id: BotEngineId
        get() = BotEngineId("Slack")

    private var session = SlackSessionFactory.createWebSocketSlackSession(configuration.token)

    suspend override fun start(botPipelines: BotPipelines) {
        session.connect()
        session.addMessagePostedListener { event, sess ->
            launch { botPipelines[BotMessage::class].execute(BotSlackMessage(id, sess, event)) }
        }

        botPipelines[BotMessageRequest::class].intercept {
            if (it.engineId != id) {
                return@intercept
            }

            val channel = session.findChannelById(it.channelId)
            session.sendMessage(channel, createSlackMessage(it.message))
        }
    }

    override fun stop() {
        session.disconnect()
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