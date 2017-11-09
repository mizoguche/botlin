package info.mizoguche.botlin.storage

import info.mizoguche.botlin.feature.BotFeatureId

interface Storable {
    fun set(id: BotFeatureId, body: String)
    fun get(id: BotFeatureId): String?
}

interface BotStorage : Storable {
    suspend fun start()
    fun stop()
}

interface BotStorageFactory<out C : Any> {
    fun create(configure: C.() -> Unit = {}): BotStorage
}
