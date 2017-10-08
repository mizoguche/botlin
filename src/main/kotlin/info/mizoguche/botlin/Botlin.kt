package info.mizoguche.botlin

interface BotlinEvent<out T> {
    suspend fun execute(): T
}

class Botlin {
    private val features = mutableListOf<BotlinFeature>()
    val subscriptions = mutableMapOf<Class<*>, MutableSet<Any>>()

    fun <C : Any, F : BotlinFeature, G : BotlinFeatureFactory<C, F>> install(factory: G, configure: C.() -> Unit = {}): F {
        val feature = factory.create(configure)
        features.add(feature)
        return feature
    }

    inline fun <reified T : BotlinEvent<*>> on(subscriber: BotlinSubscriber<T, *>) {
        val clazz = T::class.java
        subscriptions[clazz]?.add(subscriber) ?: subscriptions.put(clazz, mutableSetOf(subscriber))
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : BotlinEvent<R>, R> publish(event: T): R? {
        var result: R? = null
        var responder: BotlinFeatureId? = null
        subscriptions[event.javaClass]?.forEach {
            val subscriber = it as? BotlinSubscriber<T, R> ?: return@forEach
            if (responder == null) {
                throw IllegalStateException("${subscriber.id} tried to respond $event but already responded by $responder")
            }
            responder = subscriber.id
            result = subscriber.onPublishing(event)
        }

        if (result == null) {
            println("no subscribers for $event")
        }

        return result
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
