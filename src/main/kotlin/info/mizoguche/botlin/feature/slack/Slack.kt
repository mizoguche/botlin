package info.mizoguche.botlin.feature.slack

import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import info.mizoguche.botlin.Botlin
import info.mizoguche.botlin.BotlinFeature
import info.mizoguche.botlin.BotlinFeatureFactory
import info.mizoguche.botlin.BotlinFeatureId

class SlackMessageEvent(private val event: SlackMessagePosted, val session: SlackSession) : SlackMessagePosted by event

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
                botlin.publish<SlackMessageEvent>(SlackMessageEvent(event, sess))
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