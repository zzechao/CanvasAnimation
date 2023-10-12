package com.base.animation


fun <T> tryCatch(
    catchBlock: (Throwable) -> Unit = { t -> Animer.log.i("TryEx", "tryCatchLogcat print:", t) },
    tryBlock: () -> T
): T? {
    try {
        return tryBlock()
    } catch (t: Throwable) {
        catchBlock(t)
    }
    return null
}

suspend fun <T> tryCatchSuspend(
    catchBlock: (Throwable) -> Unit = { t -> Animer.log.i("TryEx", "tryCatchLogcat print:", t) },
    tryBlock: suspend () -> T
): T? {
    try {
        return tryBlock()
    } catch (t: Throwable) {
        catchBlock(t)
    }
    return null
}

/**
 * try catch运行block，如果有异常则再运行，直接超时times的次数
 */
fun <R> tryRepeat(times: Int, block: (Int) -> R): R? {
    var currentTimes = 0
    while (currentTimes < times) {
        try {
            return block(currentTimes)
        } catch (e: Throwable) {
            currentTimes++
        }
    }
    return null
}