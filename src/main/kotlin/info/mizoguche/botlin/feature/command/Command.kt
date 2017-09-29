package info.mizoguche.botlin.feature.command

import info.mizoguche.botlin.Botlin
import info.mizoguche.botlin.BotlinFeature
import info.mizoguche.botlin.BotlinFeatureFactory
import info.mizoguche.botlin.BotlinFeatureId
import info.mizoguche.botlin.BotlinMessageEvent
import info.mizoguche.botlin.publishing

class BotlinCommand(val msgEvent: BotlinMessageEvent) {
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

    override fun stop(botlin: Botlin) { }

    override fun start(botlin: Botlin) {
        botlin.on<BotlinMessageEvent>(publishing {
            if (it.isMention) {
                botlin.publish<BotlinCommand>(BotlinCommand(it))
            }
        })
    }

    class Configuration

    companion object Factory : BotlinFeatureFactory<Configuration, Command> {
        override fun create(configure: Configuration.() -> Unit): Command {
            val conf = Configuration().apply(configure)
            return Command(conf)
        }
    }
}

data class CommandFeatureRegister(val feature: CommandFeature)

abstract class CommandFeature : BotlinFeature {
    abstract val command: String
    abstract val description: String
    abstract val usage: String

    override fun start(botlin: Botlin) {
        onStart(botlin)

        botlin.publish<CommandFeatureRegister>(CommandFeatureRegister(this))

        botlin.on<BotlinCommand>(publishing {
            if (it.command == command) {
                onCommandPublishing(it)
            }
        })
    }

    override fun stop(botlin: Botlin) {
        onStop(botlin)
    }

    abstract fun onCommandPublishing(command: BotlinCommand)
    abstract fun onStart(botlin: Botlin)
    abstract fun onStop(botlin: Botlin)
}

class CommandHelp(conf: Configuration) : CommandFeature() {
    private val help = StringBuilder()

    override fun onStart(botlin: Botlin) {
        botlin.on<CommandFeatureRegister>(publishing {
            help.appendln("${it.feature.command}: ${it.feature.description}")
            help.appendln("Usage".prependIndent("  "))
            help.appendln("${it.feature.usage.prependIndent()}")
        })
    }

    override fun onStop(botlin: Botlin) {
    }

    override fun onCommandPublishing(command: BotlinCommand) {
        command.msgEvent.reply("```\n$help```")
    }

    override val command: String
        get() = "help"
    override val description: String
        get() = "show helps of installed commands"
    override val usage: String
        get() = "help - display this message"
    override val id: BotlinFeatureId
        get() = BotlinFeatureId("Help")

    class Configuration

    companion object Factory : BotlinFeatureFactory<Configuration, CommandHelp> {
        override fun create(configure: Configuration.() -> Unit): CommandHelp {
            val conf = Configuration().apply(configure)
            return CommandHelp(conf)
        }
    }
}