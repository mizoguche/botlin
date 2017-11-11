package info.mizoguche.botlin

import kotlin.reflect.KClass

class Pipelines(val pipelines: MutableMap<KClass<*>, Any> = mutableMapOf()) {
    inline operator fun <T : Any> get(key: KClass<T>): Pipeline<T> {
        if (pipelines.containsKey(key)) {
            return pipelines[key] as Pipeline<T>
        }
        val pipeline = Pipeline<T>()
        pipelines[key] = pipeline
        return pipeline
    }
}