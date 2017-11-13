package info.mizoguche.botlin.feature.echo

import info.mizoguche.botlin.BotMessageRequest
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureContext
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.feature.BotFeatureId
import info.mizoguche.botlin.feature.command.BotMessageCommand

class Echo : BotFeature {
    override val id: BotFeatureId
        get() = BotFeatureId("Echo")

    override val requiredFeatures: Set<BotFeatureId>
        get() = setOf(BotFeatureId("MessageCommand"))

    override fun install(context: BotFeatureContext) {
        context.pipelines[BotMessageCommand::class].intercept {
            if (it.command == "echo") {
                val request = BotMessageRequest(it.engineId, it.channelId, it.args)
                context.pipelines[BotMessageRequest::class].execute(request)
            }
        }
    }

    companion object Factory : BotFeatureFactory<Unit> {
        override fun create(configure: Unit.() -> Unit): Echo {
            return Echo()
        }
    }
}