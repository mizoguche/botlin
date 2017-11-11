package info.mizoguche.botlin.feature.command

import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureContext
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.feature.BotFeatureId

interface BotMessageCommand {
    val command: String
    val args: String
    val message: BotMessage
}

data class BotMessageCommandImpl(override val message: BotMessage) : BotMessageCommand {
    override val command: String = if (message.message.indexOf(" ") > -1) {
        message.message.split(" ")[0]
    } else {
        message.message
    }

    override val args = if (message.message.indexOf(" ") > -1) {
        message.message.replace("$command ", "")
    } else {
        ""
    }
}

class MessageCommand : BotFeature {
    override val id: BotFeatureId
        get() = BotFeatureId("command")

    override fun install(context: BotFeatureContext) {
        context.pipelines[BotMessage::class].intercept {
            if (it.isMention) {
                val command = BotMessageCommandImpl(it)
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

