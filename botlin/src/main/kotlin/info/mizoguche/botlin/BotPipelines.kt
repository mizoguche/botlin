package info.mizoguche.botlin

import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

class BotPipelines(
    private val parentScope: CoroutineScope,
    private val pipelines: MutableMap<KClass<*>, Any> = mutableMapOf()
) {
    inline fun <reified T : Any> pipelineOf(): BotPipeline<T> {
        return get(T::class)
    }

    operator fun <T : Any> get(key: KClass<T>): BotPipeline<T> {
        if (pipelines.containsKey(key)) {
            @Suppress("UNCHECKED_CAST")
            return pipelines[key] as BotPipeline<T>
        }
        val pipeline = BotPipeline<T>(parentScope)
        pipelines[key] = pipeline
        return pipeline
    }
}