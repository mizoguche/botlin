package info.mizoguche.botlin

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals

class PipelinesSpec : Spek({
    describe("Pipelines#add") {
        on("add command pipeline") {
            it("should store passed pipeline") {
                val pipelines = Pipelines()
                val pipeline = BotMessagePipeline()
                pipelines.add(pipeline)
                assertEquals(1, pipelines.count())
            }
        }
    }
})