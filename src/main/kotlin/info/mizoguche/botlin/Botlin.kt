package info.mizoguche.botlin

import kotlinx.coroutines.experimental.async

interface BotlinRequest<out T> {
    suspend fun execute(): BotlinResponse<T>
}

interface BotlinResponse<out T> {
    val result: T
}

class Botlin {
    private val features = mutableListOf<BotlinFeature>()
    val subscriptions = mutableMapOf<Class<*>, MutableSet<Any>>()

    fun <C : Any, F : BotlinFeature, G : BotlinFeatureFactory<C, F>> install(factory: G, configure: C.() -> Unit = {}): F {
        val feature = factory.create(configure)
        features.add(feature)
        return feature
    }

    inline fun <reified T : Any> on(subscriber: BotlinSubscriber<T>) {
        val clazz = T::class.java
        subscriptions[clazz]?.add(subscriber) ?: subscriptions.put(clazz, mutableSetOf(subscriber))
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> publish(event: T) {
        subscriptions[event.javaClass]?.forEach {
            val subscriber = it as? BotlinSubscriber<T> ?: return@forEach
            subscriber.onPublishing(event)
        }
    }

    fun <T> request(request: BotlinRequest<T>): BotlinResponse<T> {
        return async { request.execute() }.getCompleted()
    }

    fun start() {
        try {
            features.forEach { it.start(this) }
            while (true) {
                Thread.sleep(1000)
            }
        } finally {
            features.forEach { it.stop(this) }
        }
    }
}

fun botlin(configure: Botlin.() -> Unit = {}): Botlin {
    return Botlin().apply(configure)
}
