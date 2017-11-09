package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.engine.MessageInterceptor
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

class BotlinSpec : Spek({
    val engine = mockk<BotEngine>()
    val engineFactory = spyk<BotEngineFactory<Unit>>()

    every { engineFactory.create(any()) } returns engine
    coEvery { engine.start(any()) } returns Unit

    describe("Botlin#install(BotEngine)") {
        on("install BotEngine") {
            it("should call BotEngine#start") {
                startBotlin {
                    install(engineFactory)
                }

                coVerify { engine.start(any()) }
            }
        }
    }

    describe("Botlin#intercept") {
        val runnable = mockk<Runnable>()
        val interceptor: MessageInterceptor = { runnable.run() }
        every { runnable.run() } returns Unit

        on("intercept") {
            it("should call BotEngine#start") {
                val factory = MockEngineFactory()
                startBotlin {
                    install(factory)
                    intercept(interceptor)
                }

                val message = spyk<BotMessage>()
                factory.instance.post(message)

                verify { runnable.run() }
            }
        }
    }

    describe("Botlin#install(BotFeature)") {
        val feature = mockk<BotFeature>()
        val featureFactory = object : BotFeatureFactory<Unit> {
            override fun create(configure: Unit.() -> Unit): BotFeature {
                return feature
            }
        }

        every { feature.install(any()) } returns Unit

        on("install") {
            it("should send message to interceptor of installed feature") {
                startBotlin {
                    install(engineFactory)
                    install(featureFactory)
                }

                verify { feature.install(any()) }
            }
        }
    }
})
