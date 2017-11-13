package info.mizoguche.botlin.feature.command

import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.engine.BotEngineId
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureContext
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.feature.BotFeatureId

data class BotMessageCommand(val engineId: BotEngineId, val channelId: String, private val wholeCommand: String) {
    constructor(message: BotMessage) : this(message.engineId, message.channelId, message.message)

    val command: String = if (wholeCommand.indexOf(" ") > -1) {
        wholeCommand.split(" ")[0]
    } else {
        wholeCommand
    }

    val args = if (wholeCommand.indexOf(" ") > -1) {
        wholeCommand.replace("$command ", "")
    } else {
        ""
    }
}

class MessageCommand : BotFeature {
    override val id: BotFeatureId
        get() = BotFeatureId("MessageCommand")

    override val requiredFeatures: Set<BotFeatureId>
        get() = setOf()

    override fun install(context: BotFeatureContext) {
        context.pipelines[BotMessage::class].intercept {
            if (it.isMention) {
                val command = BotMessageCommand(it)
                context.pipelines[BotMessageCommand::class].execute(command)
            }
        }
    }

    companion object Factory : BotFeatureFactory<Unit> {
        override fun create(configure: Unit.() -> Unit): BotFeature {
            return MessageCommand()
        }
    }
}

