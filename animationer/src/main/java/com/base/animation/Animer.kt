package com.base.animation

import com.base.animation.log.DefaultLog
import com.base.animation.log.ILog
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author:zhouzechao
 * @date: 2/8/21
 * description：
 */
const val AnimThreadName: String = "anim_%d"
const val CalculationThreadName: String = "calculation_%d"

object Animer {
    var log: ILog = DefaultLog()

    private val animThreadFactory = object : ThreadFactory {
        private val mThreadId =
            AtomicInteger(0)

        override fun newThread(r: Runnable): Thread {
            val t = Thread(r)
            t.name = String.format(AnimThreadName, mThreadId.getAndIncrement())
            return t
        }
    }

    private val calculationThreadFactory = object : ThreadFactory {
        private val mThreadId =
            AtomicInteger(0)

        override fun newThread(r: Runnable): Thread {
            val t = Thread(r)
            t.name = String.format(CalculationThreadName, mThreadId.getAndIncrement())
            return t
        }
    }

    fun initLog(iLog: ILog) {
        log = iLog
    }

    val animDispatcher = ThreadPoolExecutor(
        1, 1, 1000L, TimeUnit.MILLISECONDS, LinkedBlockingDeque(), animThreadFactory
    ).asCoroutineDispatcher()

    val calculationDispatcher = ThreadPoolExecutor(
        1, 1, 1000L, TimeUnit.MILLISECONDS, LinkedBlockingDeque(), calculationThreadFactory
    ).asCoroutineDispatcher()
}