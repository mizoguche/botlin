package info.mizoguche.botlin.storage

import info.mizoguche.botlin.feature.BotFeatureId
import redis.clients.jedis.Jedis
import java.net.URI

class RedisStorage(private val configuration: Configuration) : BotStorage {
    private var jedis: Jedis? = null

    suspend override fun start() {
        val session = with(configuration) {
            Jedis(uri, timeout)
        }
        jedis = session
    }

    override fun stop() {
        jedis?.disconnect()
    }

    override fun set(id: BotFeatureId, content: String) {
        jedis?.set(id.value, content)
    }

    override fun get(id: BotFeatureId): String? {
        return jedis?.get(id.value)
    }

    class Configuration {
        lateinit var uri: URI
        var timeout = 10000
    }

    companion object Factory : BotStorageFactory<Configuration> {
        override fun create(configure: Configuration.() -> Unit): BotStorage {
            val conf = Configuration().apply(configure)
            return RedisStorage(conf)
        }
    }
}
