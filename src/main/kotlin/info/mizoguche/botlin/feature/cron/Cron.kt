package info.mizoguche.botlin.feature.cron

import com.google.gson.Gson
import info.mizoguche.botlin.BotMessageRequest
import info.mizoguche.botlin.engine.BotEngineId
import info.mizoguche.botlin.feature.BotFeature
import info.mizoguche.botlin.feature.BotFeatureContext
import info.mizoguche.botlin.feature.BotFeatureFactory
import info.mizoguche.botlin.feature.BotFeatureId
import info.mizoguche.botlin.feature.command.BotMessageCommand
import java.util.regex.Matcher
import java.util.regex.Pattern

data class Schedule(val id: Int, private val engineId: String, private val channelId: String, val cron: String, private val command: String) {
    override fun toString(): String {
        return "${id.toString().padStart(4, ' ')}: \"$cron\" $command"
    }

    fun start(context: BotFeatureContext) {
        val com = BotMessageCommand(BotEngineId(engineId), channelId, command)
        context.pipelineOf<BotMessageCommand>().execute(com)
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

private val addCommandPattern = Pattern.compile("add \"(.+? .+? .+? .+? .+?)\" (.+)")
private val removeCommandPattern = Pattern.compile("remove (\\d+?)")

private fun BotFeatureContext.post(engineId: BotEngineId, channelId: String, message: String) {
    val request = BotMessageRequest(engineId, channelId, message)
    pipelineOf<BotMessageRequest>().execute(request)
}

class Cron(configuration: Configuration) : BotFeature {
    override val requiredFeatures: Set<BotFeatureId>
        get() = setOf(BotFeatureId("MessageCommand"))
    private val scheduler = configuration.scheduler
    private val gson = Gson()

    override val id: BotFeatureId
        get() = BotFeatureId("Cron")

    override fun install(context: BotFeatureContext) {
        context.pipelineOf<BotMessageCommand>().intercept {
            if (it.command != "cron") {
                return@intercept
            }

            if (it.args == "list") {
                val schedules = currentSchedules(context)
                context.post(it.engineId, it.channelId, schedules.toString())
                return@intercept
            }

            val matcherAdd = addCommandPattern.matcher(it.args)
            if (matcherAdd.matches()) {
                add(context, matcherAdd, it)
                return@intercept
            }
            val matcherRemove = removeCommandPattern.matcher(it.args)
            if (matcherRemove.matches()) {
                remove(context, matcherRemove, it)
                return@intercept
            }

            context.post(it.engineId, it.channelId, "error: invalid args: ${it.args}. confirm cron tab.")
        }

        val schedules = currentSchedules(context)
        schedules.schedules.forEach { schedule ->
            scheduler.start(schedule) { schedule.start(context) }
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
        val content = "${matcher.group(2)}"
        val schedule = Schedule(scheduler.createScheduleId(), command.engineId.value, command.channelId, cron, content)
        val schedules = currentSchedules(context)
        schedules.schedules.add(schedule)
        storeSchedules(context, schedules)
        scheduler.start(schedule) { schedule.start(context) }
        context.post(command.engineId, command.channelId, """
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
            context.post(command.engineId, command.channelId, """
                    |Removed schedule.
                    |
                    |Current schedules:
                    |$schedules
                    """.trimMargin())
        } else {
            context.post(command.engineId, command.channelId, """
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
