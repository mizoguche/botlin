package info.mizoguche.botlin

import kotlin.reflect.KClass

class Pipelines(val pipelines: MutableMap<KClass<*>, Any> = mutableMapOf()) {
    operator fun <T : Any> get(key: KClass<T>): Pipeline<T> {
        if (pipelines.containsKey(key)) {
            @Suppress("UNCHECKED_CAST")
            return pipelines[key] as Pipeline<T>
        }
        val pipeline = Pipeline<T>()
        pipelines[key] = pipeline
        return pipeline
    }
}