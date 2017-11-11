package info.mizoguche.botlin.feature

import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.MockEngineFactory
import info.mizoguche.botlin.PipelineInterceptor
import info.mizoguche.botlin.feature.command.BotCommand
import info.mizoguche.botlin.feature.command.Command
import info.mizoguche.botlin.startBotlin
import io.mockk.every
import io.mockk.mockk
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CommandSpec : Spek({
    describe("Command#install") {
        on("install") {
            it("should intercept BotMessage") {
                val message = mockk<BotMessage>()
                val command = "command"
                val args = "args"
                every { message.message } returns "$command $args"
                every { message.isMention } returns true

                var result: BotCommand? = null
                val interceptor: PipelineInterceptor<BotCommand> = { result = it }

                val engineFactory = MockEngineFactory()
                startBotlin {
                    install(engineFactory)
                    install(Command)
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