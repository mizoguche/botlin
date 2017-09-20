package info.mizoguche.botlin.example

import info.mizoguche.botlin.publishing
import info.mizoguche.botlin.botlin
import info.mizoguche.botlin.slack.Slack
import info.mizoguche.botlin.slack.SlackMessageEvent

fun main(args: Array<String>) {
    botlin {
        install(Slack) {
            token = System.getenv("SLACK_TOKEN")
        }

        on<SlackMessageEvent>(publishing {
            if (it.messageContent == "PING") {
                it.session.sendMessage(it.channel, "PONG")
            }
        })
    }.start()
}
