package com.base.animation.helper

import com.base.animation.AnimCache
import com.base.animation.Animer
import com.base.animation.IAnimListener
import com.base.animation.IAnimView
import com.base.animation.IClickIntercept
import com.base.animation.helper.data.PathProcess
import com.base.animation.helper.data.PathProcessItem
import com.base.animation.item.BaseDisplayItem
import com.base.animation.model.AnimPathObject
import com.base.animation.model.BaseAnimDrawObject
import com.base.animation.model.DrawObject2
import com.base.animation.model.toAnimDrawObject2
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@ObsoleteCoroutinesApi
class PathObjectDeal2(private val iAnimView: IAnimView) : IPathObjectDeal {

    private val animDisplayScope = CoroutineScope(Animer.calculationDispatcher)

    /**
     * 路径坐标
     */
    override val animDrawObjects: MutableMap<Long, BaseAnimDrawObject> = ConcurrentHashMap()

    /**
     * 点击事件列表
     */
    override val clickIntercepts = mutableListOf<IClickIntercept>()

    /**
     * 动画事件
     */
    override val animListeners = mutableListOf<IAnimListener>()

    private val animDrawIds: CopyOnWriteArrayList<Long> = CopyOnWriteArrayList()

    private var loggingExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        Animer.log.e("CoroutineException", "Coroutine exception occurred. $context", throwable)
    }

    /**
     * 计算路径上的各个坐标点
     */
    private val animPather = animDisplayScope.actor<AnimPathObject>(
        Animer.calculationDispatcher + loggingExceptionHandler,
        capacity = 300
    ) {
        supervisorScope {
            for (animPath in this@actor) {
                launch(coroutineContext) {
                    if (animPath.displayItemsMap.isNotEmpty()) {
                        AnimCache.displayItemCache.putDisplayItems(animPath.displayItemsMap)
                    }
                    val drawObject = DrawObject2(animPath.animId)
                    val drawPathProcessMap = mutableMapOf<Int, List<PathProcess>>()
                    for ((index, starts) in animPath.startPoints) {
                        if (starts.isNotEmpty()) {
                            val pathProcesses = animPath.animPathMap[index]?.let {
                                it.mapIndexed { index, pathObjectWithDer ->
                                    val duringTime = pathObjectWithDer.during
                                    val start = starts[index]
                                    val startAnimObject = start.toAnimDrawObject2()
                                    val end = pathObjectWithDer.pathObject
                                    val endAnimObject = end.toAnimDrawObject2()
                                    val totalX = endAnimObject.point.x - startAnimObject.point.x
                                    val totalY = endAnimObject.point.y - startAnimObject.point.y
                                    val totalAlpha = endAnimObject.alpha - startAnimObject.alpha
                                    val totalScaleX = endAnimObject.scaleX - startAnimObject.scaleX
                                    val totalScaleY = endAnimObject.scaleY - startAnimObject.scaleY
                                    val totalRotation =
                                        endAnimObject.rotation - startAnimObject.rotation
                                    val pathProcessItem =
                                        PathProcessItem(
                                            totalX,
                                            totalY,
                                            totalAlpha,
                                            totalScaleX,
                                            totalScaleY,
                                            totalRotation
                                        )
                                    PathProcess(
                                        startAnimObject,
                                        start.interpolator, duringTime, 0f, pathProcessItem
                                    )
                                }
                            }
                            pathProcesses?.let {
                                drawPathProcessMap[index] = it
                            }
                        }
                    }
                    drawObject.animDraws = drawPathProcessMap
                    animDrawObjects[drawObject.animId] = drawObject
                    animDrawIds.add(drawObject.animId)
                }
            }
        }
    }


    /**
     * 获取显示的displayItem
     */
    override fun getDisplayItem(displayItemId: String): BaseDisplayItem? {
        return AnimCache.displayItemCache.getDisplayItem(displayItemId)
    }

    /**
     * 加入路径，并刷新画布缓存策略的时间
     */
    override fun sendAnimPath(animPathObject: AnimPathObject) {
        animPather.offer(animPathObject)
    }

    /**
     * 是否有任务
     */
    override fun hasTask(): Boolean {
        return animDrawIds.isNotEmpty()
    }


    /**
     * 清空执行中ids
     */
    override fun removeAnimId(animId: Long) {
        animDisplayScope.launch {
            animDrawIds.remove(animId)
            animDrawObjects.remove(animId)
            if (animDrawIds.isEmpty()) {
                iAnimView.pause()
            }
        }
    }
}