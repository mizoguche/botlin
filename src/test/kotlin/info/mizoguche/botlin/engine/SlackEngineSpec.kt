package info.mizoguche.botlin.engine

import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.join
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SlackEngineSpec : Spek({
    val engine = SlackEngine()
    describe("SlackEngine") {
        on("intercept") {
            it("should store interceptor") {
                engine.intercept { }
                assertEquals(1, engine.interceptors.size)
            }
        }

        on("execute message") {
            it("should call interceptor") {
                var receivedMessage: BotMessage? = null
                val message = object : BotMessage {
                }
                engine.intercept {
                    receivedMessage = it
                }

                join {
                    engine.execute(message)
                }

                assertNotNull(receivedMessage)
                assertEquals(message, receivedMessage)
            }
        }
    }
})