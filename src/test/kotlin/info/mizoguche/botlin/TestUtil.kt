package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
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
    var pipelines: Pipelines? = null

    suspend override fun start(pipelines: Pipelines) {
        this.pipelines = pipelines
    }

    override fun stop() {
    }

    inline fun <reified T : Any> post(message: T) {
        runBlocking {
            pipelines?.get(T::class)!!.execute(message)
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
