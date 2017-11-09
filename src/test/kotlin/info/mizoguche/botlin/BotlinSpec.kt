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

class BotlinSpec : Spek({
    val engine = object : BotEngine {
        var receivedInterceptor: MessageInterceptor? = null
        var receivedMessage: BotMessage? = null

        override fun intercept(interceptor: MessageInterceptor) {
            receivedInterceptor = interceptor
        }

        override inline fun execute(message: BotMessage): Job {
            receivedMessage = message

            return launch { receivedInterceptor?.invoke(message)
            }
        }
    }

    val factory = object : BotEngineFactory<Unit, BotEngine> {
        override fun create(configure: Unit.() -> Unit): BotEngine {
            return engine
        }
    }

    describe("add interceptor") {
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
            it("should send message to interceptor") {
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

    describe("install feature") {
        it("should send message to feature") {
            val feature = object : BotFeature {
                override fun install(engine: BotEngine) {
                    println("install called")
                    engine.intercept {
                        println("intercepted")
                        receivedMessage = it
                    }
                }

                var receivedMessage: BotMessage? = null
                override val id: BotFeatureId
                    get() = BotFeatureId("test")
            }

            val featureFactory = object : BotFeatureFactory<Unit, BotFeature> {
                override fun create(configure: Unit.() -> Unit): BotFeature {
                    return feature
                }
            }

            val message = object : BotMessage {}

            botlin {
                installEngine(factory)
                install(featureFactory)
            }

            join { engine.execute(message) }

            assertEquals(message, feature.receivedMessage)
        }
    }
})
