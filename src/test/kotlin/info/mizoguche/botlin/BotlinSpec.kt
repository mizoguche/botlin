package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.storage.BotStorage
import info.mizoguche.botlin.storage.BotStorageFactory
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
    describe("Botlin#install(BotEngine)") {
        val engine = mockk<BotEngine>()
        val engineFactory = spyk<BotEngineFactory<Unit>>()

        every { engineFactory.create(any()) } returns engine
        coEvery { engine.start(any()) } returns Unit

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
                factory.engine.post(message)

                verify { runnable.run() }
            }
        }
    }

    describe("Botlin#install(BotFeature)") {
        val feature = mockk<BotFeature<BotMessage>>()
        val featureFactory = object : BotFeatureFactory<BotMessage, Unit> {
            override fun create(configure: Unit.() -> Unit): BotFeature<BotMessage> {
                return feature
            }
        }

        every { feature.install(any()) } returns Unit

        on("install") {
            it("should send message to interceptor of installed feature") {
                botlin {
                    install(MockEngineFactory())
                    install(featureFactory)
                }

                verify { feature.install(any()) }
            }
        }
    }

    describe("Botlin#install(BotStorage)") {
        val storage = mockk<BotStorage>()
        val storageFactory = object : BotStorageFactory<Unit> {
            override fun create(configure: Unit.() -> Unit): BotStorage {
                return storage
            }
        }

        coEvery { storage.start() } returns Unit

        on("install") {
            it("should start storage") {
                botlin {
                    install(MockEngineFactory())
                    install(storageFactory)
                }

                coVerify { storage.start() }
            }
        }
    }
})
