package info.mizoguche.botlin.engine

import info.mizoguche.botlin.BotPipelines
import io.ktor.application.Application
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.util.concurrent.TimeUnit

class KtorEngine(private val configuration: Configuration) : BotEngine {
    private lateinit var application: ApplicationEngine

    override val id: BotEngineId
        get() = BotEngineId("Ktor")

    suspend override fun start(botPipelines: BotPipelines) {
        application = embeddedServer(Netty, port = configuration.port, module = configuration.ktorConfiguration)
                .apply { start(wait = true) }
    }

    override fun stop() {
        application.stop(3, 30, TimeUnit.SECONDS)
    }

    class Configuration {
        var port = 8080
        lateinit var ktorConfiguration: Application.() -> Unit
    }

    companion object Factory : BotEngineFactory<Configuration> {
        override fun create(configure: Configuration.() -> Unit): BotEngine {
            val conf = Configuration().apply(configure)
            return KtorEngine(conf)
        }
    }
}