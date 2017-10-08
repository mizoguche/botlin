package info.mizoguche.botlin

data class BotlinFeatureId(val value: String)

interface BotlinFeature {
    val id: BotlinFeatureId
    fun start(botlin: Botlin)
    fun stop(botlin: Botlin)
}

interface BotlinFeatureFactory<out C : Any, out F : BotlinFeature> {
    fun create(configure: C.() -> Unit = {}): F
}

interface BotlinSubscriber<in T, out R> {
    val id: BotlinFeatureId
    fun onPublishing(event: T): R
}

fun <T, R> publishing(id: BotlinFeatureId, p: (T) -> R) = object : BotlinSubscriber<T, R> {
    override val id = id

    override fun onPublishing(event: T): R {
        return p(event)
    }
}
