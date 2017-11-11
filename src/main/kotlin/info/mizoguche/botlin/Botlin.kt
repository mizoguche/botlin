package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureContext
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.storage.BotStorage
import info.mizoguche.botlin.storage.BotStorageFactory
import info.mizoguche.botlin.storage.MemoryStorage
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

class BotEngineException(message: String) : Exception(message)

class Botlin(var storage: BotStorage = MemoryStorage()) {
    private var engine: BotEngine? = null
    val pipelines = Pipelines()

    fun <TConf, TFactory : BotFeatureFactory<TConf>> install(factory: TFactory, configure: TConf.() -> Unit = {}): BotFeature {
        val feature = factory.create(configure)
        feature.install(BotFeatureContext(feature.id, this))
        return feature
    }

    fun <TConf, TFactory : BotEngineFactory<TConf>> install(factory: TFactory, configure: TConf.() -> Unit = {}): BotEngine {
        val engine = factory.create(configure)
        this.engine = engine
        return engine
    }

    fun <TConf, TFactory : BotStorageFactory<TConf>> install(factory: TFactory, configure: TConf.() -> Unit = {}): BotStorage {
        this.storage.stop()

        val storage = factory.create(configure)
        this.storage = storage
        runBlocking { storage.start() }
        return storage
    }

    inline fun <reified T : Any> intercept(noinline interceptor: PipelineInterceptor<T>) {
        pipelines[T::class].intercept(interceptor)
    }

    fun start() {
        try {
            if (engine == null) {
                throw BotEngineException("No engine is installed")
            }

            launch {
                engine?.start(pipelines)
            }
            while (true) {
                Thread.sleep(1000)
            }
        } finally {
            engine?.stop()
        }
    }
}

fun botlin(configure: Botlin.() -> Unit = {}): Botlin {
    return Botlin().apply(configure)
}
