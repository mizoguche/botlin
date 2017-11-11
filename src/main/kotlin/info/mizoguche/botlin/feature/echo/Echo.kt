package info.mizoguche.botlin.feature.echo

import info.mizoguche.botlin.Pipelines
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.feature.BotFeatureId
import info.mizoguche.botlin.feature.command.BotMessageCommand

class Echo : BotFeature {
    override val id: BotFeatureId
        get() = BotFeatureId("echo")

    override fun install(pipelines: Pipelines) {
        pipelines[BotMessageCommand::class].intercept {
            if (it.command == "echo") {
                it.message.reply(it.args)
            }
        }
    }

    companion object Factory : BotFeatureFactory<Unit> {
        override fun create(configure: Unit.() -> Unit): Echo {
            return Echo()
        }
    }
}