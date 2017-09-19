package info.mizoguche.botlin

interface BotlinFeature {
    fun start(botlin: Botlin)
}

interface BotlinFeatureFactory<out C : Any, out F : BotlinFeature> {
    fun create(configuration: C.() -> Unit): F
}

interface BotlinSubscriber<in T> {
    fun onPublish(event: T)
}

fun <T> Publish(p: (T) -> Unit) = object : BotlinSubscriber<T> {
    override fun onPublish(event: T) {
        p(event)
    }
}

