package info.mizoguche.botlin.feature.command

import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.Pipelines
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.feature.BotFeatureId

data class BotCommand(val message: BotMessage) {
    val command: String
    val args: String

    init {
        command = if (message.message.indexOf(" ") > -1) {
            message.message.split(" ")[0]
        } else {
            message.message
        }

        args = if (message.message.indexOf(" ") > -1) {
            message.message.replace("$command ", "")
        } else {
            ""
        }
    }
}

class Command : BotFeature {
    override val id: BotFeatureId
        get() = BotFeatureId("command")

    override fun install(pipelines: Pipelines) {
        pipelines[BotMessage::class].intercept {
            if (it.isMention) {
                val command = BotCommand(it)
                pipelines[BotCommand::class].execute(command)
            }
        }
    }

    companion object Factory : BotFeatureFactory<Unit> {
        override fun create(configure: Unit.() -> Unit): BotFeature {
            return Command()
        }
    }
}

