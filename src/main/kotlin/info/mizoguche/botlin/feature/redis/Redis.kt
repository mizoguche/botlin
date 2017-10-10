package info.mizoguche.botlin.feature.redis

import info.mizoguche.botlin.Botlin
import info.mizoguche.botlin.BotlinFeature
import info.mizoguche.botlin.BotlinFeatureFactory
import info.mizoguche.botlin.BotlinFeatureId
import info.mizoguche.botlin.publishing
import redis.clients.jedis.Jedis

data class BotlinStoreSetRequest(val featureId: BotlinFeatureId, val value: String)
data class BotlinStoreGetRequest(val featureId: BotlinFeatureId, val callback: (String?) -> Unit)

class Redis(private val configuration: Redis.Configuration) : BotlinFeature {
    override val id: BotlinFeatureId
        get() = BotlinFeatureId("Redis")

    private var jedis: Jedis? = null

    override fun stop(botlin: Botlin) {
        jedis?.disconnect()
    }

    override fun start(botlin: Botlin) {
        val session = with(configuration) {
            Jedis(host, port, timeout)
        }
        jedis = session

        botlin.on<BotlinStoreSetRequest>(publishing {
            session.set(it.featureId.value, it.value)
        })

        botlin.on<BotlinStoreGetRequest>(publishing {
            val value = session.get(it.featureId.value)
            it.callback(value)
        })
    }

    class Configuration {
        var host = ""
        var port = 6379
        var timeout = 10000
    }

    companion object Factory : BotlinFeatureFactory<Configuration, Redis> {
        override fun create(configure: Configuration.() -> Unit): Redis {
            val conf = Configuration().apply(configure)
            return Redis(conf)
        }
    }
}
