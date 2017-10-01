package info.mizoguche.botlin.feature.command

import info.mizoguche.botlin.Botlin
import info.mizoguche.botlin.BotlinFeature
import info.mizoguche.botlin.BotlinFeatureFactory
import info.mizoguche.botlin.BotlinFeatureId
import info.mizoguche.botlin.publishing

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
    open fun onStart(botlin: Botlin) {
        println("Start feature: $command")
    }

    open fun onStop(botlin: Botlin) {
        println("Stop feature: $command")
    }
}

class CommandHelp(conf: Configuration) : CommandFeature() {
    private val help = StringBuilder()

    override fun onStart(botlin: Botlin) {
        botlin.on<CommandFeatureRegister>(publishing {
            help.append("""
                |${it.feature.command}: ${it.feature.description}
                |${it.feature.usage.prependIndent()}
                |
                |
                """.trimMargin())
        })
    }

    override fun onCommandPublishing(command: BotlinCommand) {
        command.msgEvent.reply("```\n${help.replace(Regex.fromLiteral("<botlin>"), "${command.msgEvent.session.mentionPrefix}").trimEnd('\n')}```")
    }

    override val command: String
        get() = "help"
    override val description: String
        get() = "show helps of installed commands"
    override val usage: String
        get() = """
            |<botlin> help
            |    display this message
            """.trimMargin()
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