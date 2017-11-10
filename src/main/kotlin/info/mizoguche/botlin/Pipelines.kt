package info.mizoguche.botlin

import kotlin.reflect.KClass

class Pipelines(val pipelines: MutableMap<KClass<*>, Pipeline<*>> = mutableMapOf()) : Collection<Pipeline<*>> by pipelines.values {
    inline fun <reified T : Any> add(pipeline: Pipeline<T>) {
        pipelines.put(T::class, pipeline)
    }
}