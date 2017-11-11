package info.mizoguche.botlin.feature

import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.MockEngineFactory
import info.mizoguche.botlin.createMockCommand
import info.mizoguche.botlin.feature.cron.Cron
import info.mizoguche.botlin.feature.cron.CronScheduler
import info.mizoguche.botlin.startBotlin
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

class CronSpec : Spek({
    describe("Echo#install") {
        on("add schedule") {
            it("should call CronScheduler.add") {
                val message = mockk<BotMessage>()
                val command = createMockCommand(
                        command = "cron",
                        args = "\"* * * * *\" @botlin echo cron",
                        message = message
                )
                every { message.reply("Schedule \"${command.args}\"") } returns Unit
                val mockScheduler = mockk<CronScheduler>()
                every { mockScheduler.start(any(), any()) } returns Unit

                val engineFactory = MockEngineFactory()
                startBotlin {
                    install(engineFactory)
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