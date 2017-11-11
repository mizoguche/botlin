package info.mizoguche.botlin.storage

import info.mizoguche.botlin.feature.BotFeatureId

interface Storable {
    fun set(id: BotFeatureId, content: String)
    fun get(id: BotFeatureId): String?
}

interface BotStorage {
    suspend fun start()
    fun stop()
    fun set(id: BotFeatureId, content: String)
    fun get(id: BotFeatureId): String?
}

interface BotStorageFactory<out C : Any> {
    fun create(configure: C.() -> Unit = {}): BotStorage
}
