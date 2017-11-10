package info.mizoguche.botlin.feature.cron

//class ListCommand(val id: BotlinFeatureId, val command: BotlinCommand) : Subcommand {
//    override fun execute() {
//        val storeGetReq = BotlinStoreGetRequest(id) {
//            val schedules = gson.fromJson(it, Schedules::class.java) ?: Schedules(mutableListOf())
//            command.msgEvent.reply(schedules.toString())
//        }
//        command.botlin.publish(storeGetReq)
//    }
//}
