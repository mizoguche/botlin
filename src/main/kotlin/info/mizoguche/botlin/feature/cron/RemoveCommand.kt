package info.mizoguche.botlin.feature.cron

import info.mizoguche.botlin.BotlinFeatureId
import info.mizoguche.botlin.feature.command.BotlinCommand

class RemoveCommand(val id: BotlinFeatureId, val command: BotlinCommand, val scheduleId: Int) : Subcommand {
    override fun execute() {
//        val storeGetReq = BotlinStoreGetRequest(id) {
//            val schedules = gson.fromJson(it, Schedules::class.java) ?: Schedules(mutableListOf())
//            schedules.schedules.removeIf { it.id == scheduleId }
//            stopSchedule(scheduleId)
//            val json = gson.toJson(schedules)
//            val setReq = BotlinStoreSetRequest(id, json)
//            command.botlin.publish(setReq)
//            command.msgEvent.reply("""
//                    |Removed schedule.
//                    |
//                    |Current schedules:
//                    |$schedules
//                    """.trimMargin())
//        }
//        command.botlin.publish(storeGetReq)
    }
}
