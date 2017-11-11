package info.mizoguche.botlin

import kotlin.reflect.KClass

class BotPipelines(val pipelines: MutableMap<KClass<*>, Any> = mutableMapOf()) {
    operator fun <T : Any> get(key: KClass<T>): BotPipeline<T> {
        if (pipelines.containsKey(key)) {
            @Suppress("UNCHECKED_CAST")
            return pipelines[key] as BotPipeline<T>
        }
        val pipeline = BotPipeline<T>()
        pipelines[key] = pipeline
        return pipeline
    }
}