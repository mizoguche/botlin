package info.mizoguche.botlin

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BotPipelinesSpec : Spek({
    describe("BotPipelines#get") {
        on("get pipeline") {
            it("should return pipeline") {
                val pipelines = BotPipelines()
                assertNotNull(pipelines[BotMessage::class])
            }
        }

        on("get pipeline multiple times") {
            it("should return same pipeline") {
                val pipelines = BotPipelines()
                val pipeline = pipelines[BotMessage::class]
                assertEquals(pipeline, pipelines[BotMessage::class])
            }
        }
    }
})