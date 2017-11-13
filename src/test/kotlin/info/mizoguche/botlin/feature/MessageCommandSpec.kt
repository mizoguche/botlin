package info.mizoguche.botlin.feature

import info.mizoguche.botlin.BotPipelineInterceptor
import info.mizoguche.botlin.MockEngineFactory
import info.mizoguche.botlin.createMockCommandMessage
import info.mizoguche.botlin.feature.command.BotMessageCommand
import info.mizoguche.botlin.feature.command.MessageCommand
import info.mizoguche.botlin.startBotlin
import io.mockk.junit.MockKJUnit4Runner
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(MockKJUnit4Runner::class)
class MessageCommandSpec : Spek({
    describe("MessageCommand#install") {
        on("install") {
            it("should intercept BotMessage and proceed MessageCommand") {
                val message = createMockCommandMessage("command args")

                var result: BotMessageCommand? = null
                val interceptor: BotPipelineInterceptor<BotMessageCommand> = { result = it }

                val engineFactory = MockEngineFactory()
                startBotlin {
                    install(engineFactory)
                    install(MessageCommand)
                    intercept(interceptor)
                }

                engineFactory.engine.post(message)

                assertEquals("command", result?.command)
                assertEquals("args", result?.args)
            }
        }
    }
})