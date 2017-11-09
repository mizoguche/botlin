package info.mizoguche.botlin.storage

interface BotStorage {
    suspend fun start()
    fun stop()
}

interface BotStorageFactory<out C : Any> {
    fun create(configure: C.() -> Unit = {}): BotStorage
}
