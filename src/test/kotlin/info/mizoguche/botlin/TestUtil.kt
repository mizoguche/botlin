package info.mizoguche.botlin

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

fun join(coroutine: () -> Job) = runBlocking {
    coroutine().join()
}

fun startBotlin(configure: Botlin.() -> Unit) {
    val job = launch {
        botlin(configure).start()
    }
    Thread.sleep(100)
    job.cancel()

}
