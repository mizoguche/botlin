package info.mizoguche.botlin

import info.mizoguche.botlin.engine.BotEngine
import info.mizoguche.botlin.engine.BotEngineFactory
import info.mizoguche.botlin.pipeline.BotMessagePipeline
import kotlinx.coroutines.experimental.launch
import sun.plugin.dom.exception.InvalidStateException

class Botlin {
    private var engine: BotEngine? = null
    private val messagePipeline = BotMessagePipeline()

    fun <C : Any, F : BotFeature, G : BotFeatureFactory<C, F>> install(factory: G, configure: C.() -> Unit = {}): F {
        val feature = factory.create(configure)
        feature.install(messagePipeline)
        return feature
    }

    fun <C : Any, E : BotEngine, F : BotEngineFactory<C, E>> installEngine(factory: F, configure: C.() -> Unit = {}): E {
        val e = factory.create(configure)
        engine = e
        return e
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
