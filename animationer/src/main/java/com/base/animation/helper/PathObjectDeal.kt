package com.base.animation.helper

import android.graphics.PointF
import com.base.animation.AnimCache
import com.base.animation.Animer
import com.base.animation.IAnimListener
import com.base.animation.IAnimView
import com.base.animation.IClickIntercept
import com.base.animation.cache.DisplayItemCache
import com.base.animation.cache.PathCache
import com.base.animation.item.BaseDisplayItem
import com.base.animation.model.AnimDrawObject
import com.base.animation.model.AnimPathObject
import com.base.animation.model.DrawObject
import com.base.animation.model.FIRST_START_POSITION
import com.base.animation.model.toAnimDrawObject
import com.google.common.cache.CacheBuilder
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

/**
 * @author:zhouzechao
 * @date: 2020/12/9
 * description：处理animPathObject转每一帧的绘制点
 */
private const val TAG = "PathObjectDeal"

@ObsoleteCoroutinesApi
class PathObjectDeal(private val iAnimView: IAnimView) {

    /**
     * 每帧时间
     */
    var intervalDeal: Long = 0L

    private val animDisplayScope = CoroutineScope(Animer.calculationDispatcher)

    /**
     * 路径坐标
     */
    val animDrawObjects: MutableMap<Long, DrawObject> = ConcurrentHashMap()

    /**
     * 点击事件列表
     */
    val clickIntercepts = mutableListOf<IClickIntercept>()

    /**
     * 动画事件
     */
    val animListeners = mutableListOf<IAnimListener?>()

    private val animDrawIds: CopyOnWriteArrayList<Long> = CopyOnWriteArrayList()

    /**
     * 路径缓存
     */
    private val pathCacheMap: com.google.common.cache.Cache<String,
            MutableMap<Int, MutableList<AnimDrawObject>>> =
        CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .maximumSize(10)
            .initialCapacity(5)
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .build()

    /**
     * 画布缓存时间策略
     */
    @Volatile
    var lastCacheTime = 0L
    var cacheTime = 10 * 1000L
        set(value) {
            field = if (value < 10) {
                10 * 1000L
            } else {
                value * 1000L
            }
        }

    /**
     * 路径坐标缓存
     */
    private val pathCachePools = PathCache()

    private var loggingExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        Animer.log.e("CoroutineException", "Coroutine exception occurred. $context", throwable)
    }

    /**
     * 计算路径上的各个坐标点
     */
    private val animPather = animDisplayScope.actor<AnimPathObject>(
        coroutineContext + loggingExceptionHandler,
        capacity = 300
    ) {
        supervisorScope {
            for (animPath in this@actor) {
                launch(coroutineContext) {
                    Animer.log.i("tttt1", "calculation start")
                    if (animPath.displayItemsMap.isNotEmpty()) {
                        AnimCache.displayItemCache.putDisplayItems(animPath.displayItemsMap)
                    }
                    val cacheKey = pathCachePools.conventKey(intervalDeal, animPath)
                    val drawsMap = mutableMapOf<Int, MutableList<AnimDrawObject>>()
                    var position = 0
                    val drawObject = DrawObject(animPath.animId)
                    if (pathCacheMap.getIfPresent(cacheKey) != null) {
                        Animer.log.i(TAG, "cache used")
                        pathCacheMap.getIfPresent(cacheKey)?.map {
                            val animDrawObjects = mutableListOf<AnimDrawObject>()
                            it.value.forEach {
                                animDrawObjects.add(
                                    AnimDrawObject(
                                        displayItemId = "",
                                        point = it.point,
                                        alpha = it.alpha,
                                        scaleX = it.scaleX,
                                        scaleY = it.scaleY,
                                        rotation = it.rotation,
                                        clickable = false,
                                        expand = ""
                                    )
                                )
                            }
                            drawsMap.put(it.key, animDrawObjects)
                        }
                        for ((index, starts) in animPath.startPoints) {
                            var startPosition = position
                            if (starts.isNotEmpty()) {
                                animPath.animPathMap[index]?.apply {
                                    val duringTime = during
                                    val times = ceil(duringTime / intervalDeal.toDouble()).toInt()
                                    pathObjects.forEachIndexed { index, pathObject ->
                                        val start =
                                            if (pathObject.parentPointPosition == FIRST_START_POSITION) {
                                                starts.first()
                                            } else {
                                                starts.firstOrNull() {
                                                    it.pointPosition == pathObject.parentPointPosition
                                                } ?: starts.first()
                                            }
                                        drawsMap[startPosition]?.map {
                                            it.displayItemId = start.displayItemId
                                        }
                                        for (i in 0..times) {
                                            startPosition++
                                            drawsMap[startPosition]?.map {
                                                it.displayItemId = start.displayItemId
                                                it.clickable = animPath.clickable
                                                it.expand = animPath.expand
                                            }
                                        }
                                        // 当索引为这轮终点的长度时，如果是进入下一轮计算，否则继续执行这一轮的计算，从这轮的那个position开始计算
                                        if (index == pathObjects.size - 1) {
                                            position = startPosition
                                            position++
                                        } else {
                                            startPosition = position
                                        }
                                    }
                                }
                            }
                        }
                        drawObject.animDraws = drawsMap
                        animDrawObjects[drawObject.animId] = drawObject
                        animDrawIds.add(drawObject.animId)
                        Animer.log.i("tttt1", "calculation end")
                    } else {
                        Animer.log.i(TAG, "no cache used")
                        for ((index, starts) in animPath.startPoints) {
                            var startPosition = position
                            if (starts.isNotEmpty()) {
                                animPath.animPathMap[index]?.apply {
                                    val duringTime = during
                                    val times = ceil(duringTime / intervalDeal.toDouble()).toInt()
                                    pathObjects.forEachIndexed { index, pathObject ->
                                        val start =
                                            if (pathObject.parentPointPosition == FIRST_START_POSITION) {
                                                starts.first()
                                            } else {
                                                starts.firstOrNull() {
                                                    it.pointPosition == pathObject.parentPointPosition
                                                } ?: starts.first()
                                            }
                                        if (drawsMap[startPosition] == null) {
                                            drawsMap[startPosition] = mutableListOf()
                                        }
                                        drawsMap[startPosition]?.add(
                                            start.toAnimDrawObject(
                                                animPath.clickable,
                                                animPath.expand
                                            )
                                        )
                                        pathObject.getItem(start)
                                        for (i in 0..times) {
                                            startPosition++
                                            var p = i * intervalDeal.toFloat() / duringTime
                                            if (p > 1f) {
                                                p = 1f
                                            }
                                            val interP = start.interpolator.getInterpolation(p)
                                            val animDrawObject = AnimDrawObject(
                                                start.displayItemId,
                                                clickable = animPath.clickable,
                                                expand = animPath.expand
                                            ).apply {
                                                point =
                                                    PointF(
                                                        start.point.x + pathObject.itemX * interP,
                                                        start.point.y + pathObject.itemY * interP
                                                    )
                                                alpha =
                                                    start.alpha + (pathObject.itemAlpha * interP).toInt()
                                                scaleX =
                                                    start.scaleX + pathObject.itemScaleX * interP
                                                scaleY =
                                                    start.scaleY + pathObject.itemScaleY * interP
                                                rotation =
                                                    start.rotation + pathObject.itemRotation * interP
                                            }
                                            if (drawsMap[startPosition] == null) {
                                                drawsMap[startPosition] = mutableListOf()
                                            }
                                            drawsMap[startPosition]?.add(animDrawObject)
                                        }
                                        // 当索引为这轮终点的长度时，如果是进入下一轮计算，否则继续执行这一轮的计算，从这轮的那个position开始计算
                                        if (index == pathObjects.size - 1) {
                                            position = startPosition
                                            position++
                                        } else {
                                            startPosition = position
                                        }
                                    }
                                }
                            }
                        }
                        if (animPath.pathCache && cacheKey.isNotEmpty()) {
                            pathCacheMap.put(cacheKey, drawsMap)
                        }
                        drawObject.animDraws = drawsMap
                        animDrawObjects[drawObject.animId] = drawObject
                        animDrawIds.add(drawObject.animId)
                        Animer.log.i("tttt1", "calculation end")
                    }
                }
            }
        }
    }

    /**
     * 获取显示的displayItem
     */
    fun getDisplayItem(displayItemId: String): BaseDisplayItem? {
        return AnimCache.displayItemCache.getDisplayItem(displayItemId)
    }

    /**
     * 加入路径，并刷新画布缓存策略的时间
     */
    fun sendAnimPath(animPathObject: AnimPathObject) {
        lastCacheTime = cacheTime
        animPather.offer(animPathObject)
    }

    /**
     * 清除数据
     */
    fun release() {
        animDrawObjects.clear()
        AnimCache.displayItemCache.clear()
        clickIntercepts.clear()
        animListeners.clear()
        animDrawIds.clear()
        animPather.close()
    }

    /**
     * 是否有任务
     */
    fun hasTask(): Boolean {
        return animDrawIds.isNotEmpty()
    }


    /**
     * 清空执行中ids
     */
    fun removeAnimId(animId: Long) {
        animDisplayScope.launch {
            animDrawIds.remove(animId)
            animDrawObjects.remove(animId)
            if (animDrawIds.isEmpty()) {
                iAnimView.pause()
            }
        }
    }
}


