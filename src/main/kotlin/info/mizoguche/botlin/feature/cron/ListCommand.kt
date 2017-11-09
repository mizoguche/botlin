package info.mizoguche.botlin.feature.cron

import info.mizoguche.botlin.BotlinFeatureId
import info.mizoguche.botlin.feature.command.BotlinCommand
import info.mizoguche.botlin.feature.redis.BotlinStoreGetRequest

class ListCommand(val id: BotlinFeatureId, val command: BotlinCommand) : Subcommand {
    override fun execute() {
        val storeGetReq = BotlinStoreGetRequest(id) {
            val schedules = gson.fromJson(it, Cron.Schedules::class.java) ?: Cron.Schedules(mutableListOf())
            command.msgEvent.reply(schedules.toString())
        }
//        command.botlin.publish(storeGetReq)
    }
}
