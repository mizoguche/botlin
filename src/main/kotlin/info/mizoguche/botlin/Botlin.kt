package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.storage.BotStorage
import info.mizoguche.botlin.storage.BotStorageFactory
import info.mizoguche.botlin.storage.MemoryStorage
import info.mizoguche.botlin.storage.Storable
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

class Botlin(private var storage: BotStorage = MemoryStorage()) : Storable by storage {
    private var engine: BotEngine? = null
    val pipelines = Pipelines()

    inline fun <reified TContext : Any, TConf : Any, TFactory : BotFeatureFactory<TContext, TConf>> install(factory: TFactory, noinline configure: TConf.() -> Unit = {}): BotFeature<TContext> {
        val feature = factory.create(configure)
        val pipeline = pipelines[TContext::class]
        feature.install(pipeline)
        return feature
    }

    fun <TConf : Any, TFactory : BotEngineFactory<TConf>> install(factory: TFactory, configure: TConf.() -> Unit = {}): BotEngine {
        val engine = factory.create(configure)
        this.engine = engine
        return engine
    }

    fun <TConf : Any, TFactory : BotStorageFactory<TConf>> install(factory: TFactory, configure: TConf.() -> Unit = {}): BotStorage {
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
                throw Exception("No engine is installed")
            }

            launch {
                engine?.start {
                    pipelines[BotMessage::class].execute(it)
                }
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
