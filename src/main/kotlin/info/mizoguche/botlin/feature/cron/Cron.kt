package info.mizoguche.botlin.feature.cron

import com.google.gson.Gson
import info.mizoguche.botlin.Botlin
import info.mizoguche.botlin.BotlinFeatureFactory
import info.mizoguche.botlin.BotlinFeatureId
import info.mizoguche.botlin.BotlinMessageRequest
import info.mizoguche.botlin.feature.command.BotlinCommand
import info.mizoguche.botlin.feature.command.CommandFeature
import info.mizoguche.botlin.feature.redis.BotlinStoreGetRequest
import info.mizoguche.botlin.feature.redis.BotlinStoreSetRequest
import it.sauronsoftware.cron4j.Scheduler
import java.util.Random
import java.util.regex.Pattern

class Cron : CommandFeature() {
    override val command: String
        get() = "cron"
    override val description: String
        get() = "Set schedule"
    override val usage: String
        get() = """
            |ex. Let botlin echo hello at every morning 10:00AM(Required echo command)
            |cron add \"* 10 * * *\" @botlin echo hello
            |   adds a new schedule
            |
            |cron list
            |   show added schedules
            |
            |cron remove <schedule id>
            |   delete the specified schedule
            """.trimMargin()

    private val schedulers: Map<Int, Scheduler> = HashMap()
    private val random: Random = Random()

    private fun createScheduleId(): Int {
        if (schedulers.count() == MAX_SCHEDULES) {
            throw IllegalStateException("schedule count is at capacity")
        }

        var idCandidate = random.nextInt(10000)
        while (schedulers.containsKey(idCandidate)) {
            idCandidate = random.nextInt(10000)
        }

        return idCandidate
    }

    private val gson = Gson()

    private data class Schedule(val id: Int, val channelId: String, val cron: String, val command: String)
    private data class Schedules(val schedules: MutableList<Schedule>)

    private fun parse(command: BotlinCommand): Schedule {
        val id = createScheduleId()
        val matcher = ADD_COMMAND_PATTERN.matcher(command.args)
        if (matcher.matches()) {
            val cron = matcher.group(1)
            val com = "${command.msgEvent.session.mentionPrefix} ${matcher.group(2)}"
            return Schedule(id, command.msgEvent.channelId, cron, com)
        }
        throw IllegalArgumentException("invalid args: ${command.args}")
    }

    override fun onCommandPublishing(command: BotlinCommand) {
        try {
            val schedule = parse(command)
            val botlin = command.botlin

            val storeGetReq = BotlinStoreGetRequest(id) {
                val schedules = gson.fromJson<Schedules>(it, Schedules::class.java) ?: Schedules(mutableListOf())
                schedules.schedules.add(schedule)
                val json = gson.toJson(schedules)
                val setReq = BotlinStoreSetRequest(id, json)
                botlin.publish<BotlinStoreSetRequest>(setReq)
                startSchedule(botlin, schedule)
            }
            command.botlin.publish<BotlinStoreGetRequest>(storeGetReq)

            command.msgEvent.reply("schedule created.")
        } catch (e: IllegalArgumentException) {
            command.msgEvent.reply("error: invalid args. confirm cron tab.")
        }
    }

    private fun startSchedule(botlin: Botlin, schedule: Schedule) {
        val scheduler = Scheduler().apply {
            schedule(schedule.cron) {
                botlin.publish<BotlinMessageRequest>(BotlinMessageRequest(
                        channelId = schedule.channelId,
                        message = schedule.command
                ))
            }
        }
        scheduler.start()
    }

    override fun onStart(botlin: Botlin) {
        val storeGetReq = BotlinStoreGetRequest(id) {
            val schedules = gson.fromJson<Schedules>(it, Schedules::class.java) ?: Schedules(mutableListOf())
            schedules.schedules.forEach {
                println(it)
                startSchedule(botlin, it)
            }
        }
        botlin.publish<BotlinStoreGetRequest>(storeGetReq)
    }

    override val id: BotlinFeatureId
        get() = BotlinFeatureId("Cron")


    class Configuration

    companion object Factory : BotlinFeatureFactory<Configuration, Cron> {
        private val ADD_COMMAND_PATTERN = Pattern.compile("add \"(.+? .+? .+? .+? .+?)\" (.+)")
        val MAX_SCHEDULES = 10000

        override fun create(configure: Configuration.() -> Unit): Cron {
            return Cron()
        }
    }
}