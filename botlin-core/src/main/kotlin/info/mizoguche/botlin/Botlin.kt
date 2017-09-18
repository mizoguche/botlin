package info.mizoguche.botlin

import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import info.mizoguche.botlin.Slack.Configuration

fun botlin(configure: Botlin.() -> Unit = {}): Botlin {
    return Botlin().apply(configure)
}

interface BotlinFeature<C : Any> {
    fun start()
}

interface BotlinFeatureFactory<C : Any, out F : BotlinFeature<C>> {
    fun create(configuration: C.() -> Unit): F
}

class Slack(val configuration: Slack.Configuration) : BotlinFeature<Configuration> {
    override fun start() {
        val session = SlackSessionFactory.createWebSocketSlackSession(configuration.token)
        session.connect()
        session.channels.
                filter { it.isMember }
                .forEach { session.sendMessage(it, "Hello.") }
    }

    class Configuration {
        var token = ""
    }

    companion object Factory : BotlinFeatureFactory<Configuration, Slack> {
        override fun create(configuration: Configuration.() -> Unit): Slack {
            val conf = Configuration().apply(configuration)
            return Slack(conf)
        }
    }
}

class Botlin {
    private val features = mutableListOf<BotlinFeature<*>>()

    fun <C : Any, F : BotlinFeature<C>, G : BotlinFeatureFactory<C, F>> install(factory: G, configure: C.() -> Unit): F {
        val feature = factory.create(configure)
        features.add(feature)
        return feature
    }

    fun start() {
        features.forEach(BotlinFeature<*>::start)
    }
}