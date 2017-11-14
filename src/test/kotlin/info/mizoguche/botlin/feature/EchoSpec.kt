package info.mizoguche.botlin.feature

import info.mizoguche.botlin.BotMessageRequest
import info.mizoguche.botlin.BotPipelineInterceptor
import info.mizoguche.botlin.MockEngineFactory
import info.mizoguche.botlin.createMockCommand
import info.mizoguche.botlin.feature.command.MessageCommand
import info.mizoguche.botlin.feature.echo.Echo
import info.mizoguche.botlin.startBotlin
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals

class EchoSpec : Spek({
    describe("Echo#install") {
        on("install") {
            it("should intercept BotMessage") {
                val command = createMockCommand("echo body")

                var interceptedMessage: BotMessageRequest? = null
                val interceptor: BotPipelineInterceptor<BotMessageRequest> = { interceptedMessage = it }

                val engineFactory = MockEngineFactory()
                startBotlin {
                    install(engineFactory)
                    install(MessageCommand)
                    install(Echo)
                    intercept(interceptor)
                }

                engineFactory.engine.post(command)

                assertEquals("body", interceptedMessage?.message)
            }
        }
    }
})