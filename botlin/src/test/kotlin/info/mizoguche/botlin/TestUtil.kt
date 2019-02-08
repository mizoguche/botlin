package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.engine.BotEngineId
import info.mizoguche.botlin.feature.command.BotMessageCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun startBotlin(configure: Botlin.() -> Unit): Botlin {
    val botlin = botlin(configure)
    val job = GlobalScope.launch {
        botlin.start()
    }
    Thread.sleep(400)
    job.cancel()
    return botlin
}

fun createMockCommandMessage(message: String): BotMessage {
    return object : BotMessage {
        override val engineId: BotEngineId
            get() = BotEngineId("mock")
        override val channelId: String
            get() = "test"
        override val message: String
            get() = message
        override val rawMessage: String
            get() = "@botlin $message"
        override val sender: BotMessageSender
            get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.
        override val session: BotMessageSession
            get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.

        override fun reply(body: String) {
            TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
        }
    }
}

fun createMockCommand(wholeCommand: String): BotMessageCommand {
    return BotMessageCommand(BotEngineId(""), "", wholeCommand)
}

class MockEngine : BotEngine {
    override val id: BotEngineId
        get() = BotEngineId("mock")
    var botPipelines: BotPipelines? = null

    override suspend fun start(botPipelines: BotPipelines) {
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

    override fun create(parentScope: CoroutineScope, configure: Unit.() -> Unit): BotEngine {
        return engine
    }
}
