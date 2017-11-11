package info.mizoguche.botlin.feature.echo

import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.Pipeline
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.feature.BotFeatureId

class Echo : BotFeature<BotMessage> {
    override val id: BotFeatureId
        get() = BotFeatureId("echo")

    override fun install(pipeline: Pipeline<BotMessage>) {
        pipeline.intercept {
            it.reply(it.message)
        }
    }

    companion object Factory : BotFeatureFactory<BotMessage, Unit> {
        override fun create(configure: Unit.() -> Unit): Echo {
            return Echo()
        }
    }
}