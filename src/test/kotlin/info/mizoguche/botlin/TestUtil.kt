package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.feature.command.BotCommand
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

fun startBotlin(configure: Botlin.() -> Unit): Botlin {
    val botlin = botlin(configure)
    val job = launch {
        botlin.start()
    }
    Thread.sleep(400)
    job.cancel()
    return botlin
}

fun createMockCommand(command: String, args: String, message: BotMessage): BotCommand {
    return object : BotCommand {
        override val command: String
            get() = command
        override val args: String
            get() = args
        override val message: BotMessage
            get() = message
    }
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
            delay(300)
        }
    }
}

class MockEngineFactory : BotEngineFactory<Unit> {
    val engine = MockEngine()

    override fun create(configure: Unit.() -> Unit): BotEngine {
        return engine
    }
}
