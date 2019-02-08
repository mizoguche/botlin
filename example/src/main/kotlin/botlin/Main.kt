package botlin

import info.mizoguche.botlin.BotMessage
import info.mizoguche.botlin.botlin
import info.mizoguche.botlin.engine.SlackEngine

fun main() {
    botlin {
        install(SlackEngine) {
            token = System.getenv("SLACK_TOKEN")
        }

        intercept<BotMessage> {
            if (it.message == "PING") {
                it.reply("PONG")
            }
        }
    }.start()
}