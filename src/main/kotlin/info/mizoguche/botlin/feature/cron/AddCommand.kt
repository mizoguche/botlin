package info.mizoguche.botlin.feature.cron

import info.mizoguche.botlin.BotlinFeatureId
import info.mizoguche.botlin.feature.command.BotlinCommand
import info.mizoguche.botlin.feature.redis.BotlinStoreGetRequest
import info.mizoguche.botlin.feature.redis.BotlinStoreSetRequest

class AddCommand(val id: BotlinFeatureId, val command: BotlinCommand, val schedule: Cron.Schedule) : Subcommand {
    override fun execute() {
        val storeGetReq = BotlinStoreGetRequest(id) {
            val schedules = gson.fromJson(it, Cron.Schedules::class.java) ?: Cron.Schedules(mutableListOf())
            schedules.schedules.add(schedule)
            val json = gson.toJson(schedules)
            val setReq = BotlinStoreSetRequest(id, json)
            command.botlin.publish(setReq)
            startSchedule(command.botlin, schedule)
            command.msgEvent.reply("""
                    |Created schedule.
                    |
                    |Current schedules:
                    |$schedules
                    """.trimMargin())
        }
        command.botlin.publish(storeGetReq)
    }
}
