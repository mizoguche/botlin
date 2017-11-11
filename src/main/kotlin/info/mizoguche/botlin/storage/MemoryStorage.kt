package info.mizoguche.botlin.storage

import info.mizoguche.botlin.feature.BotFeatureId

class MemoryStorage : BotStorage {
    private val map = mutableMapOf<BotFeatureId, String>()

    suspend override fun start() {
        map.clear()
    }

    override fun stop() {
        map.clear()
    }

    override fun set(id: BotFeatureId, content: String) {
        map.put(id, content)
    }

    override fun get(id: BotFeatureId): String? {
        return map[id]
    }

    companion object Factory : BotStorageFactory<Unit> {
        override fun create(configure: Unit.() -> Unit): BotStorage {
            return MemoryStorage()
        }
    }
}