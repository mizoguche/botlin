package info.mizoguche.botlin.storage

import info.mizoguche.botlin.feature.BotFeatureId
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.net.URI

class RedisStorage(private val configuration: Configuration) : BotStorage {
    private var jedisPool: JedisPool? = null

    private val jedis: Jedis?
        get() = jedisPool?.resource

    override suspend fun start() {
        jedisPool = with(configuration) {
            JedisPool(toJedisPoolConfig(), uri, timeout)
        }
    }

    override fun stop() {
        jedis?.disconnect()
    }

    override fun set(id: BotFeatureId, content: String) {
        jedis?.set(id.value, content)
    }

    override fun get(id: BotFeatureId): String? = jedis?.get(id.value)

    class Configuration {
        lateinit var uri: URI
        var timeout = 600
        var poolMaxToal = 10
        var poolMaxIdle = 5
        var poolMinIdle = 1
        var poolTestOnBorrow = true
        var poolTestOnReturn = true
        var poolTestWhileIdle = true

        fun toJedisPoolConfig(): JedisPoolConfig {
            return JedisPoolConfig().apply {
                maxTotal = poolMaxToal
                maxIdle = poolMaxIdle
                minIdle = poolMinIdle
                testOnBorrow = poolTestOnBorrow
                testOnReturn = poolTestOnReturn
                testWhileIdle = poolTestWhileIdle
            }
        }
    }

    companion object Factory : BotStorageFactory<Configuration> {
        override fun create(configure: Configuration.() -> Unit): BotStorage {
            val conf = Configuration().apply(configure)
            return RedisStorage(conf)
        }
    }
}
