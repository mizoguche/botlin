package info.mizoguche.botlin

class Botlin {
    private val features = mutableListOf<BotlinFeature>()
    val subscriptions = mutableMapOf<Class<*>, MutableSet<Any>>()

    fun <C : Any, F : BotlinFeature, G : BotlinFeatureFactory<C, F>> install(factory: G, configure: C.() -> Unit): F {
        val feature = factory.create(configure)
        features.add(feature)
        return feature
    }

    inline fun <reified T> on(subscriber: BotlinSubscriber<T>) {
        val clazz = T::class.java
        subscriptions[clazz]?.add(subscriber) ?: subscriptions.put(clazz, mutableSetOf(subscriber))
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> publish(event: Any) {
        subscriptions[event.javaClass]?.forEach {
            if (event is T) {
                val subscriber = it as? BotlinSubscriber<T> ?: return@forEach
                subscriber.onPublishing(event)
            }
        }
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
