package info.mizoguche.botlin.storage

import info.mizoguche.botlin.feature.BotFeatureId
import redis.clients.jedis.Jedis

class RedisStorage(private val configuration: Configuration) : BotStorage {
    private var jedis: Jedis? = null

    suspend override fun start() {
        val session = with(configuration) {
            Jedis(host, port, timeout)
        }
        jedis = session
    }

    override fun stop() {
        jedis?.disconnect()
    }

    override fun set(id: BotFeatureId, body: String) {
        jedis?.set(id.value, body)
    }

    override fun get(id: BotFeatureId): String? {
        return jedis?.get(id.value)
    }

    class Configuration {
        var host = ""
        var port = 6379
        var timeout = 10000
    }

    companion object Factory : BotStorageFactory<Configuration> {
        override fun create(configure: Configuration.() -> Unit): BotStorage {
            val conf = Configuration().apply(configure)
            return RedisStorage(conf)
        }
    }
}
