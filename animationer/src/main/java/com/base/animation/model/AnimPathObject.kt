package com.base.animation.model

import android.util.Log
import com.base.animation.item.BaseDisplayItem
import java.util.concurrent.atomic.AtomicLong

/**
 * @author:zhouzechao
 * @date: 2020/11/16
 * description：动画路劲
 */
private const val TAG = "AnimPathObject"

data class AnimPathObject @JvmOverloads constructor(
    var startPoints: MutableMap<Int, MutableList<PathObject>>,
    var animPathMap: MutableMap<Int, PathObjectsWithDer>,
    var displayItemsMap: MutableMap<String, BaseDisplayItem>,
    var pathCache: Boolean = false,
    var clickable: Boolean = false,
    var expand: String = ""
) {

    val animId = pathObjectId.incrementAndGet()

    class Inner {

        /**
         * 每个动画的开始节点
         */
        private var beginPathObjects = mutableMapOf<Int, MutableList<PathObject>>()

        /**
         * 节点
         */
        private var position = 0
        private var animPathMap = mutableMapOf<Int, PathObjectsWithDer>()
        private var pathCache: Boolean = false

        private var isInit = false
        private var isSetStart = true

        companion object {
            @JvmStatic
            fun with(): Inner {
                return Inner()
            }
        }

        private constructor()

        /**
         * 开始位置
         */
        fun beginAnimPath(
            pathObject: PathObject
        ): Inner {
            if (!isSetStart) {
                return this
            }
            isInit = true
            isSetStart = false
            beginPathObjects.clear()
            animPathMap.clear()
            position = 0
            beginPathObjects[position] = mutableListOf(pathObject)
            return this
        }


        fun beginNextAnimPath(pathObject: PathObject): Inner {
            if (!isInit) {
                throw IllegalStateException("please run beginAnimPath,next run beginNextAnimPath")
            }
            if (!isSetStart) {
                return this
            }
            isSetStart = false
            beginPathObjects[position] = mutableListOf(pathObject)
            return this
        }

        fun doAnimPath(
            during: Long, pathObject: PathObject
        ): Inner {
            if (!isInit) {
                throw IllegalStateException("please run beginAnimPath,next run beginNextAnimPath")
            }
            if (isSetStart) {
                return this
            }
            isSetStart = true
            animPathMap[position] = PathObjectsWithDer(during, listOf(pathObject))
            position++
            return this
        }

        fun doAnimPaths(
            during: Long, pathObjects: List<PathObject>
        ): Inner {
            if (!isInit) {
                throw IllegalStateException("please run beginAnimPath,next run beginNextAnimPath")
            }
            if (isSetStart) {
                return this
            }
            isSetStart = true
            animPathMap[position] = PathObjectsWithDer(during, pathObjects)
            position++
            return this
        }

        /**
         * 轨迹创建
         */
        fun build(displayItems: List<BaseDisplayItem>): AnimPathObject {
            if (!isInit) {
                throw IllegalStateException("please run beginAnimPath,next run beginNextAnimPath")
            }
            if (beginPathObjects.isEmpty()) throw IllegalStateException("please set beginAnimPath")
            if (animPathMap.isEmpty()) throw IllegalStateException("please add doNextAnimPath")
            val displayItem = displayItems.associateBy { it.displayItemId }.toMutableMap()
            Log.i("zzc","$beginPathObjects \n $animPathMap \n $displayItem")
            return AnimPathObject(
                beginPathObjects, animPathMap, displayItem, pathCache = pathCache
            )
        }
    }
}

/**
 * 路线时间
 */
data class PathObjectsWithDer(var during: Long, var pathObjects: List<PathObject>)

val pathObjectId = AtomicLong(System.currentTimeMillis() / 1000L)
