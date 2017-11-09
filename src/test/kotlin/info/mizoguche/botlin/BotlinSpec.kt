package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.engine.MessageInterceptor
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BotlinSpec : Spek({
    describe("add interceptor") {
        val engine = object : BotEngine {
            var receivedInterceptor: MessageInterceptor? = null
            var receivedMessage: BotMessage? = null

            override fun intercept(interceptor: MessageInterceptor) {
                receivedInterceptor = interceptor
            }

            inline override fun execute(message: BotMessage): Job {
                receivedMessage = message
                return launch { }
            }
        }

        val factory = object : BotEngineFactory<Unit, BotEngine> {
            override fun create(configure: Unit.() -> Unit): BotEngine {
                return engine
            }
        }

        on("installEngine") {
            it("should receive interceptor") {
                val interceptor: MessageInterceptor = {}
                botlin {
                    installEngine(factory)
                    intercept(interceptor)
                }
                assertEquals(interceptor, engine.receivedInterceptor)
            }
        }

        on("intercept") {
            it("should receive message") {
                val interceptor: MessageInterceptor = {}
                val message = object : BotMessage {}

                botlin {
                    installEngine(factory)
                    intercept(interceptor)
                }

                join { engine.execute(message) }

                assertEquals(message, engine.receivedMessage)
            }
        }
    }
})
