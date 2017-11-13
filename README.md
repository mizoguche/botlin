Botlin
===

[![CircleCI](https://circleci.com/gh/mizoguche/botlin.svg?style=svg)](https://circleci.com/gh/mizoguche/botlin)

- Bot framework built with Kotlin
- Inspired from [Ktor - asynchronous Web framework for Kotlin](http://ktor.io/)

## Usage
See [mizoguche/botlin-template](https://github.com/mizoguche/botlin-template) for example.

### Configuration example
```kotlin
fun main(args: Array<String>) {
    botlin {
        install(SlackEngine) {
            token =  System.getenv("SLACK_TOKEN")
        }

        install(RedisStorage) {
            host = "localhost"
        }

        install(MessageCommand)
        install(Echo)
        install(Cron)

        intercept<BotMessage> {
            if (it.message == "PING") {
                it.reply("PONG")
            }
        }
    }.start()
}
```

## Description
### Goal
- Easy to use
    - Readable configuration
- Easy to extend
    - You can create your original feature easily
    - You can adopt new web services as BotEngine easily

### Main objects
- BotEngine
    - Providers which receives/sends messages
    - ex. SlackEngine
- BotMessage
    - Abstract expression of messages
- BotFeature
    - Realize some useful features
    - ex. Echo, Cron
- BotPipeline
    - Any objects can flow through BotPipeline
        - BotEngines send BotMessages to BotPipeline
    - Objects flowing through BotPipeline are intercepted from BotFeature or Botlin configuration


## License
MIT