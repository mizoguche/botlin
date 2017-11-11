package info.mizoguche.botlin.feature

import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.MockEngineFactory
import info.mizoguche.botlin.BotPipelineInterceptor
import info.mizoguche.botlin.feature.command.BotMessageCommand
import info.mizoguche.botlin.feature.command.MessageCommand
import info.mizoguche.botlin.startBotlin
import io.mockk.every
import io.mockk.mockk
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MessageCommandSpec : Spek({
    describe("MessageCommand#install") {
        on("install") {
            it("should intercept BotMessage and proceed MessageCommand") {
                val message = mockk<BotMessage>()
                val command = "command"
                val args = "args"
                every { message.message } returns "$command $args"
                every { message.isMention } returns true

                var result: BotMessageCommand? = null
                val interceptor: BotPipelineInterceptor<BotMessageCommand> = { result = it }

                val engineFactory = MockEngineFactory()
                startBotlin {
                    install(engineFactory)
                    install(MessageCommand)
                    intercept(interceptor)
                }

                engineFactory.engine.post(message)

                assertNotNull(result)
                assertEquals(command, result?.command)
                assertEquals(args, result?.args)
            }
        }
    }
})