package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureContext
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.feature.BotFeatureId
import info.mizoguche.botlin.feature.command.MessageCommand
import info.mizoguche.botlin.storage.BotStorage
import info.mizoguche.botlin.storage.BotStorageFactory
import info.mizoguche.botlin.storage.MemoryStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BotEngineException(message: String) : Exception(message)
class BotFeatureException(message: String) : Exception(message)

private val preinstalledFeatures = listOf(MessageCommand)

class Botlin(
    var storage: BotStorage = MemoryStorage(),
    private val scope: CoroutineScope = GlobalScope
) {
    private var engine: BotEngine? = null
    val pipelines = BotPipelines(scope)
    private val installedFeatureIds = mutableSetOf<BotFeatureId>()

    fun <TConf, TFactory : BotFeatureFactory<TConf>> install(
        factory: TFactory,
        configure: TConf.() -> Unit = {}
    ): BotFeature {
        requireNotNull(engine) {
            throw BotEngineException("No engine is installed")
        }

        val feature = factory.create(configure)
        if (!installedFeatureIds.containsAll(feature.requiredFeatures)) {
            throw BotFeatureException("Required feature is not installed yet: ${feature.requiredFeatures.joinToString { it.value }}")
        }
        installedFeatureIds.add(feature.id)
        feature.install(BotFeatureContext(feature.id, this, engine!!))
        println("Installed feature: ${feature.id.value}")
        return feature
    }

    fun <TConf, TFactory : BotEngineFactory<TConf>> install(
        factory: TFactory,
        configure: TConf.() -> Unit = {}
    ): BotEngine {
        val engine = factory.create(scope, configure)
        this.engine = engine
        return engine
    }

    fun <TConf, TFactory : BotStorageFactory<TConf>> install(
        factory: TFactory,
        configure: TConf.() -> Unit = {}
    ): BotStorage {
        this.storage.stop()

        val storage = factory.create(configure)
        this.storage = storage
        GlobalScope.launch { storage.start() }
        return storage
    }

    inline fun <reified T : Any> intercept(noinline interceptor: BotPipelineInterceptor<T>) {
        pipelines[T::class].intercept(interceptor)
    }

    fun start() {
        try {
            preinstalledFeatures.forEach { install(it) }

            if (engine == null) {
                throw BotEngineException("No engine is installed")
            }

            scope.launch { engine?.start(pipelines) }

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
