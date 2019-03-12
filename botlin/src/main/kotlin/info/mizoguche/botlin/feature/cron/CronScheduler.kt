package info.mizoguche.botlin.feature.cron

import it.sauronsoftware.cron4j.Scheduler
import java.util.Random

interface CronScheduler {
    fun start(schedule: Schedule, action: () -> Unit)
    fun stop(scheduleId: Int)
    fun isStarted(scheduleId: Int): Boolean
    fun createScheduleId(): Int
}

private const val maxSchedules = 10000

class CronforjScheduler : CronScheduler {
    private val random: Random = Random()
    private val startedSchedules = mutableMapOf<Int, Scheduler>()

    override fun createScheduleId(): Int {
        if (startedSchedules.count() == maxSchedules) {
            throw IllegalStateException("schedule count is at capacity")
        }

        var idCandidate = random.nextInt(maxSchedules)
        while (startedSchedules.containsKey(idCandidate)) {
            idCandidate = random.nextInt(maxSchedules)
        }

        return idCandidate
    }

    override fun start(schedule: Schedule, action: () -> Unit) {
        val scheduler = Scheduler().apply {
            schedule(schedule.cron, action)
        }
        scheduler.start()
        startedSchedules[schedule.id] = scheduler
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
