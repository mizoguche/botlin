package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.engine.BotEngineId
import info.mizoguche.botlin.feature.command.BotMessageCommand
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

fun createMockCommand(command: String, args: String, message: BotMessage): BotMessageCommand {
    return object : BotMessageCommand {
        override val command: String
            get() = command
        override val args: String
            get() = args
        override val message: BotMessage
            get() = message
    }
}

class MockEngine : BotEngine {
    override val id: BotEngineId
        get() = BotEngineId("mock")
    var botPipelines: BotPipelines? = null

    suspend override fun start(botPipelines: BotPipelines) {
        this.botPipelines = botPipelines
    }

    override fun stop() {
    }

    inline fun <reified T : Any> post(message: T) {
        runBlocking {
            botPipelines?.get(T::class)!!.execute(message)
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
