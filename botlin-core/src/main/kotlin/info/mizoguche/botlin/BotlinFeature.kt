package info.mizoguche.botlin

interface BotlinFeature {
    fun start(botlin: Botlin)
}

interface BotlinFeatureFactory<out C : Any, out F : BotlinFeature> {
    fun create(configure: C.() -> Unit): F
}

interface BotlinSubscriber<in T> {
    fun onPublishing(event: T)
}

fun <T> publishing(p: (T) -> Unit) = object : BotlinSubscriber<T> {
    override fun onPublishing(event: T) {
        p(event)
    }
}
