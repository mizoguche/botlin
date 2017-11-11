package info.mizoguche.botlin.feature.cron

import com.google.gson.Gson
import info.mizoguche.botlin.BotMessageRequest
import info.mizoguche.botlin.engine.BotEngineId
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureContext
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.feature.BotFeatureId
import info.mizoguche.botlin.feature.command.BotMessageCommand
import it.sauronsoftware.cron4j.Scheduler
import java.util.Random
import java.util.regex.Matcher
import java.util.regex.Pattern

data class Schedule(val id: Int, val engineId: String, val channelId: String, val cron: String, val content: String) {
    override fun toString(): String {
        return "${id.toString().padStart(4, ' ')}: \"$cron\" $content"
    }
}

data class Schedules(val schedules: MutableList<Schedule>) {
    override fun toString(): String {
        if (schedules.count() == 0) {
            return "```\nNo schedules.\n```"
        }
        return "```\n${schedules.joinToString("\n")}\n```"
    }
}

private val ADD_COMMAND_PATTERN = Pattern.compile("add \"(.+? .+? .+? .+? .+?)\" (.+)")
private val REMOVE_COMMAND_PATTERN = Pattern.compile("remove (\\d+?)")
private val MAX_SCHEDULES = 10000

class Cron(configuration: Configuration) : BotFeature {
    private val scheduler = configuration.scheduler
    private val gson = Gson()

    override val id: BotFeatureId
        get() = BotFeatureId("cron")

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

    override fun install(context: BotFeatureContext) {
        context.pipelines[BotMessageCommand::class].intercept {
            if (it.command != "cron") {
                return@intercept
            }

            if (it.args == "list") {
                val schedules = currentSchedules(context)
                it.message.reply(schedules.toString())
                return@intercept
            }

            val matcherAdd = ADD_COMMAND_PATTERN.matcher(it.args)
            if (matcherAdd.matches()) {
                add(context, matcherAdd, it)
                return@intercept
            }
            val matcherRemove = REMOVE_COMMAND_PATTERN.matcher(it.args)
            if (matcherRemove.matches()) {
                remove(context, matcherRemove, it)
                return@intercept
            }

            it.message.reply("error: invalid args: ${it.args}. confirm cron tab.")
        }

        val schedules = currentSchedules(context)
        schedules.schedules.forEach {
            scheduler.start(it) {
                val request = BotMessageRequest(BotEngineId(it.engineId), it.channelId, it.content)
                context.pipelines[BotMessageRequest::class].execute(request)
            }
        }
    }

    private fun currentSchedules(context: BotFeatureContext): Schedules {
        val schedulesJson = context.get()
        return gson.fromJson(schedulesJson, Schedules::class.java) ?: Schedules(mutableListOf())
    }

    private fun storeSchedules(context: BotFeatureContext, schedules: Schedules) {
        val json = gson.toJson(schedules)
        context.set(json)
    }

    private fun add(context: BotFeatureContext, matcher: Matcher, command: BotMessageCommand) {
        val cron = matcher.group(1)
        val content = "${command.message.session.mentionPrefix} ${matcher.group(2)}"
        val schedule = Schedule(createScheduleId(), command.message.engineId.value, command.message.channelId, cron, content)
        val schedules = currentSchedules(context)
        schedules.schedules.add(schedule)
        storeSchedules(context, schedules)
        scheduler.start(schedule) {
            val request = BotMessageRequest(command.message.engineId, command.message.channelId, content)
            context.pipelines[BotMessageRequest::class].execute(request)
        }
        command.message.reply("""
                    |Created schedule.
                    |
                    |Current schedules:
                    |$schedules
                    """.trimMargin())
    }

    private fun remove(context: BotFeatureContext, matcher: Matcher, command: BotMessageCommand) {
        val scheduleId = matcher.group(1).toInt()
        val schedules = currentSchedules(context)
        schedules.schedules.removeIf { it.id == scheduleId }
        storeSchedules(context, schedules)
        if (scheduler.isStarted(scheduleId)) {
            scheduler.stop(scheduleId)
            command.message.reply("""
                    |Removed schedule.
                    |
                    |Current schedules:
                    |$schedules
                    """.trimMargin())
        } else {
            command.message.reply("""
                    |Schedule not found.
                    |
                    |Current schedules:
                    |$schedules
                    """.trimMargin())
        }
    }

    class Configuration {
        var scheduler: CronScheduler = CronforjScheduler()
    }

    companion object Factory : BotFeatureFactory<Configuration> {
        override fun create(configure: Configuration.() -> Unit): Cron {
            val conf = Configuration().apply(configure)
            return Cron(conf)
        }
    }
}
