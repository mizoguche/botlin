package info.mizoguche.botlin.feature.redis

import info.mizoguche.botlin.Botlin
import info.mizoguche.botlin.BotlinFeature
import info.mizoguche.botlin.BotlinFeatureFactory

class Redis(private val configuration: Redis.Configuration) : BotlinFeature {
    override fun start(botlin: Botlin) {
    }

    class Configuration {
        var token = ""
    }

    companion object Factory : BotlinFeatureFactory<Configuration, Redis> {
        override fun create(configure: Configuration.() -> Unit): Redis {
            val conf = Configuration().apply(configure)
            return Redis(conf)
        }
    }
}
