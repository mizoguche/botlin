package info.mizoguche.botlin

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.runBlocking

fun join(coroutine: () -> Job) = runBlocking {
    coroutine().join()
}

