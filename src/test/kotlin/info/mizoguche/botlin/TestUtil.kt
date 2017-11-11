package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.engine.BotMessageHandler
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

fun startBotlin(configure: Botlin.() -> Unit): Botlin {
    val botlin = botlin(configure)
    val job = launch {
        botlin.start()
    }
    Thread.sleep(100)
    job.cancel()
    return botlin
}

class MockEngine : BotEngine {
    var handler: BotMessageHandler? = null

    suspend override fun start(handler: BotMessageHandler) {
        this.handler = handler
    }

    override fun stop() {
    }

    fun post(message: BotMessage) {
        runBlocking {
            handler?.invoke(message)
            delay(100)
        }
    }
}

class MockEngineFactory : BotEngineFactory<Unit> {
    val engine = MockEngine()

    override fun create(configure: Unit.() -> Unit): BotEngine {
        return engine
    }
}
