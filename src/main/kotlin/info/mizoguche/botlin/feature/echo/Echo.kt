package info.mizoguche.botlin.feature.echo

import info.mizoguche.botlin.BotlinFeatureFactory
import info.mizoguche.botlin.BotlinFeatureId
import info.mizoguche.botlin.feature.command.BotlinCommand
import info.mizoguche.botlin.feature.command.CommandFeature

class Echo : CommandFeature() {
    override val command: String
        get() = "echo"
    override val description: String
        get() = "echo"
    override val usage: String
        get() = """
            |<botlin> echo hello
            |    post message "hello"
            """.trimMargin()

    override fun onCommandPublishing(command: BotlinCommand) {
        command.msgEvent.reply(command.args)
    }

    override val id: BotlinFeatureId
        get() = BotlinFeatureId("Echo")

    class Configuration

    companion object Factory : BotlinFeatureFactory<Configuration, Echo> {
        override fun create(configure: Configuration.() -> Unit): Echo {
            return Echo()
        }
    }
}