package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.engine.MessageInterceptor
import sun.plugin.dom.exception.InvalidStateException

class Botlin {
    private val features = mutableListOf<BotlinFeature>()
    val subscriptions = mutableMapOf<Class<*>, MutableSet<Any>>()
    private lateinit var engine: BotEngine

    fun <C : Any, F : BotFeature, G : BotFeatureFactory<C, F>> install(factory: G, configure: C.() -> Unit = {}): F {
        val feature = factory.create(configure)
        feature.install(engine)
        return feature
    }

    inline fun <reified T : Any> on(subscriber: BotlinSubscriber<T>) {
        val clazz = T::class.java
        subscriptions[clazz]?.add(subscriber) ?: subscriptions.put(clazz, mutableSetOf(subscriber))
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> publish(event: T) {
        subscriptions[event.javaClass]?.forEach {
            val subscriber = it as BotlinSubscriber<T>
            subscriber.onPublishing(event)
        }
    }

    fun <C : Any, E : BotEngine, F : BotEngineFactory<C, E>> installEngine(factory: F, configure: C.() -> Unit = {}): E {
        val e = factory.create(configure)
        engine = e
        return e
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

    fun intercept(messageInterceptor: MessageInterceptor) {
        if (engine == null) {
            throw InvalidStateException("No engine is installed")
        }
        engine.intercept(messageInterceptor)
    }
}

fun botlin(configure: Botlin.() -> Unit = {}): Botlin {
    return Botlin().apply(configure)
}
