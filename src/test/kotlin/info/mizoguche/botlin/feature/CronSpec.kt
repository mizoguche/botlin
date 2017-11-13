package info.mizoguche.botlin.feature

import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.BotMessageSession
import info.mizoguche.botlin.MockEngineFactory
import info.mizoguche.botlin.createMockCommand
import info.mizoguche.botlin.engine.BotEngineId
import info.mizoguche.botlin.feature.command.MessageCommand
import info.mizoguche.botlin.feature.cron.Cron
import info.mizoguche.botlin.feature.cron.CronScheduler
import info.mizoguche.botlin.startBotlin
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.api.dsl.xit

class CronSpec : Spek({
    describe("Cron#install") {
        on("add schedule") {
            xit("should call CronScheduler.add") {
                val message = mockk<BotMessage>()
                val command = createMockCommand("""cron add "* * * * *" echo test""")
                every { message.reply(any()) } returns Unit
                every { message.channelId } returns "test"
                val session = mockk<BotMessageSession>()
                every { session.mentionPrefix } returns "@botlin "
                every { message.session } returns session
                every { message.engineId } returns BotEngineId("test")
                val mockScheduler = mockk<CronScheduler>()
                every { mockScheduler.start(any(), any()) } returns Unit

                val engineFactory = MockEngineFactory()
                startBotlin {
                    install(engineFactory)
                    install(MessageCommand)
                    install(Cron) {
                        scheduler = mockScheduler
                    }
                }

                engineFactory.engine.post(command)

                verify { mockScheduler.start(any(), any()) }
            }
        }
    }
})