package info.mizoguche.botlin.feature.cron

import it.sauronsoftware.cron4j.Scheduler

interface CronScheduler {
    fun start(schedule: Schedule, action: () -> Unit)
    fun stop(scheduleId: Int)
    fun isStarted(scheduleId: Int): Boolean
}

class Cron4jScheduler : CronScheduler {
    private val startedSchedules = mutableMapOf<Int, Scheduler>()

    override fun start(schedule: Schedule, action: () -> Unit) {
        val scheduler = Scheduler().apply {
            schedule(schedule.cron, action)
        }
        scheduler.start()
        startedSchedules.put(schedule.id, scheduler)
    }

    override fun stop(scheduleId: Int) {
        val scheduler = startedSchedules[scheduleId] ?: return
        startedSchedules.remove(scheduleId)
        scheduler.stop()
    }

    override fun isStarted(scheduleId: Int): Boolean {
        return startedSchedules.containsKey(scheduleId)
    }
}
