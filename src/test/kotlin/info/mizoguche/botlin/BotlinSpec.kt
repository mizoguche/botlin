package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.engine.BotMessageHandler
import info.mizoguche.botlin.engine.MessageInterceptor
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals

class BotlinSpec : Spek({
    val engine = object : BotEngine {
        var handler: BotMessageHandler? = null

        suspend override fun start(handler: BotMessageHandler) {
            this.handler = handler
        }

        override fun stop() {
        }

        fun post(message: BotMessage): Job {
            return launch { handler?.invoke(message) }
        }
    }

    val factory = object : BotEngineFactory<Unit> {
        override fun create(configure: Unit.() -> Unit): BotEngine {
            return engine
        }
    }

    val message = object : BotMessage {
        override val channelId: String
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val message: String
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val rawMessage: String
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val sender: BotMessageSender
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val session: BotMessageSession
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

        override fun reply(body: String) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    describe("#intercept") {
        on("intercept") {
            it("should send message to interceptor") {
                var receivedMessage: BotMessage? = null
                val interceptor: MessageInterceptor = {
                    receivedMessage = it
                }

                val job = launch {
                    botlin {
                        installEngine(factory)
                        intercept(interceptor)
                    }.start()
                }
                job.cancel()

                join { engine.post(message) }
                assertEquals(receivedMessage, message)
            }
        }
    }
})
