package info.mizoguche.botlin.engine

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals

class SlackEngineSpec : Spek({
    val engine = SlackEngine()
    describe("SlackEngine") {
        on("intercept") {
            it("should store interceptor") {
                engine.intercept { }
                assertEquals(1, engine.interceptors.size)
            }
        }
    }
})