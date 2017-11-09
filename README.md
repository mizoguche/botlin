botlin
===

Slack bot built with Kotlin

## Usage
```kotlin
fun main(args: Array<String>) {
    botlin {
        install(SlackEngine) {
            token =  System.getenv("SLACK_TOKEN")
        }

        intercept {
            if (it.rawMessage == "PING") {
                it.reply("PONG")
            }
        }
    }.start()
}
```
