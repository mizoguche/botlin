Botlin
===

Bot framework built with Kotlin

## Usage
```kotlin
fun main(args: Array<String>) {
    botlin {
        install(SlackEngine) {
            token =  System.getenv("SLACK_TOKEN")
        }

        install(MessageCommand)
        install(Echo)
        install(Cron)

        intercept {
            if (it.message == "PING") {
                it.reply("PONG")
            }
        }
    }.start()
}
```
