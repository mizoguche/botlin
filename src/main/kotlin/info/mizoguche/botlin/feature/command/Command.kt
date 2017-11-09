package info.mizoguche.botlin.feature.command

import info.mizoguche.botlin.Botlin
import info.mizoguche.botlin.BotlinFeature
import info.mizoguche.botlin.BotlinFeatureFactory
import info.mizoguche.botlin.BotlinFeatureId
import info.mizoguche.botlin.BotlinMessageEvent
import info.mizoguche.botlin.publishing

data class BotlinCommand(val botlin: Botlin, val msgEvent: BotlinMessageEvent) {
    val command: String
        get() = if (msgEvent.message.indexOf(" ") > -1) {
            msgEvent.message.split(" ")[0]
        } else {
            msgEvent.message
        }

    val args: String
        get() = if (msgEvent.message.indexOf(" ") > -1) {
            msgEvent.message.replace("$command ", "")
        } else {
            ""
        }
}

class Command(private val configuration: Configuration) : BotlinFeature {
    override val id: BotlinFeatureId
        get() = BotlinFeatureId("Command")

    override fun stop(botlin: Botlin) {
        println("Stop Command feature")
    }

    override fun start(botlin: Botlin) {
        println("Start Command feature")

//        botlin.on(publishing<BotlinMessageEvent> {
//            if (it.isMention) {
//                botlin.publish(BotlinCommand(botlin, it))
//            }
//        })
    }

    class Configuration

    companion object Factory : BotlinFeatureFactory<Configuration, Command> {
        override fun create(configure: Configuration.() -> Unit): Command {
            val conf = Configuration().apply(configure)
            return Command(conf)
        }
    }
}

