package info.mizoguche.botlin.feature.echo

import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.Pipelines
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.feature.BotFeatureId

class Echo : BotFeature {
    override val id: BotFeatureId
        get() = BotFeatureId("echo")

    override fun install(pipelines: Pipelines) {
        pipelines[BotMessage::class].intercept {
            it.reply(it.message)
        }
    }

    companion object Factory : BotFeatureFactory<Unit> {
        override fun create(configure: Unit.() -> Unit): Echo {
            return Echo()
        }
    }
}