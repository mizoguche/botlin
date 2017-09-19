botlin
===

Slack bot built with Kotlin

## Usage
```kotlin
fun main(args: Array<String>) {
    botlin {
        install(Slack) {
            token = System.getenv("SLACK_TOKEN")
        }

        on<SlackMessageEvent>(Publish {
            if (it.messageContent == "PING") {
                it.session.sendMessage(it.channel, "PONG")
            }
        })
    }.start()
}
```
