package info.mizoguche.botlin.example

import info.mizoguche.botlin.Slack
import info.mizoguche.botlin.botlin

fun main(args: Array<String>) {
    botlin {
        install(Slack) {
            token = System.getenv("SLACK_TOKEN")
        }
    }.start()
}