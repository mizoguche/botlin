package info.mizoguche.botlin.feature

import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.MockEngineFactory
import info.mizoguche.botlin.createMockCommand
import info.mizoguche.botlin.feature.command.MessageCommand
import info.mizoguche.botlin.feature.echo.Echo
import info.mizoguche.botlin.startBotlin
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

class EchoSpec : Spek({
    describe("Echo#install") {
        on("install") {
            it("should intercept BotMessage") {
                val message = mockk<BotMessage>()
                val command = createMockCommand(
                        command = "echo",
                        args = "body",
                        message = message
                )
                every { message.reply(command.args) } returns Unit

                val engineFactory = MockEngineFactory()
                startBotlin {
                    install(engineFactory)
                    install(MessageCommand)
                    install(Echo)
                }

                engineFactory.engine.post(command)

                verify { message.reply(command.args) }
            }
        }
    }
})