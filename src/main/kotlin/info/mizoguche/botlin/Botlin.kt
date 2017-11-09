package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.storage.BotStorage
import info.mizoguche.botlin.storage.BotStorageFactory
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import sun.plugin.dom.exception.InvalidStateException

class Botlin {
    private var engine: BotEngine? = null
    private var storage: BotStorage? = null
    private val messagePipeline = BotMessagePipeline()

    fun <TConf : Any, TFactory : BotFeatureFactory<TConf>> install(factory: TFactory, configure: TConf.() -> Unit = {}): BotFeature {
        val feature = factory.create(configure)
        feature.install(messagePipeline)
        return feature
    }

    fun <TConf : Any, TFactory : BotEngineFactory<TConf>> install(factory: TFactory, configure: TConf.() -> Unit = {}): BotEngine {
        val engine = factory.create(configure)
        this.engine = engine
        return engine
    }

    fun <TConf : Any, TFactory : BotStorageFactory<TConf>> install(factory: TFactory, configure: TConf.() -> Unit = {}): BotStorage {
        val storage = factory.create(configure)
        this.storage = storage
        runBlocking { storage.start() }
        return storage
    }

    fun intercept(messageInterceptor: MessageInterceptor) {
        messagePipeline.intercept(messageInterceptor)
    }

    fun start() {
        try {
            if (engine == null) {
                throw InvalidStateException("No engine is installed")
            }

            launch {
                engine?.start {
                    messagePipeline.execute(it)
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
