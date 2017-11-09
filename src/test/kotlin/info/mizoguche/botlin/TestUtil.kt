package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.engine.BotMessageHandler
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

fun join(coroutine: () -> Job) = runBlocking {
    coroutine().join()
}

fun startBotlin(configure: Botlin.() -> Unit) {
    val job = launch {
        botlin(configure).start()
    }
    Thread.sleep(100)
    job.cancel()

}

class MockEngine : BotEngine {
    var handler: BotMessageHandler? = null

    suspend override fun start(handler: BotMessageHandler) {
        this.handler = handler
    }

    override fun stop() {
    }

    fun post(message: BotMessage) {
        runBlocking { handler?.invoke(message) }
    }
}

class MockEngineFactory : BotEngineFactory<Unit> {
    val instance = MockEngine()

    override fun create(configure: Unit.() -> Unit): BotEngine {
        return instance
    }
}
