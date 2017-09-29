package info.mizoguche.botlin.feature.slack

import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import info.mizoguche.botlin.Botlin
import info.mizoguche.botlin.BotlinFeature
import info.mizoguche.botlin.BotlinFeatureFactory
import info.mizoguche.botlin.BotlinFeatureId
import info.mizoguche.botlin.BotlinMessageEvent
import info.mizoguche.botlin.BotlinMessageSender

class Slack(private val configuration: Slack.Configuration) : BotlinFeature {
    override val id: BotlinFeatureId
        get() = BotlinFeatureId("Slack")

    private var slackSession: SlackSession? = null

    override fun stop(botlin: Botlin) {
        slackSession?.disconnect()
    }

    override fun start(botlin: Botlin) {
        val session = SlackSessionFactory.createWebSocketSlackSession(configuration.token)
        slackSession = session
        session.connect()
        session.addMessagePostedListener { event, sess ->
            if (event.sender.id != session.sessionPersona().id) {
                val e = BotlinMessageEvent(
                        message = event.messageContent.replace("<@${session.sessionPersona().id}> ", ""),
                        rawMessage = event.messageContent,
                        sender = BotlinMessageSender(
                                senderId = event.sender.id,
                                senderUserName = event.sender.userName,
                                senderDisplayName = event.sender.realName
                        ),
                        reply = { session.sendMessage(event.channel, it) }
                )
                botlin.publish<BotlinMessageEvent>(e)
            }
        }
    }

    class Configuration {
        var token = ""
    }

    companion object Factory : BotlinFeatureFactory<Configuration, Slack> {
        override fun create(configure: Configuration.() -> Unit): Slack {
            val conf = Configuration().apply(configure)
            return Slack(conf)
        }
    }
}