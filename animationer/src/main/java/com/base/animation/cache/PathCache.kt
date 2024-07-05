package com.base.animation.cache

import com.base.animation.model.AnimPathObject
import java.security.MessageDigest

/**
 * @author:zhouzechao
 * @date: 1/26/21
 * description：路径坐标缓存
 */
class PathCache {

    private val md by lazy { MessageDigest.getInstance("MD5") }

    /**
     * 根据每帧时间和路径组成唯一的key去判断路径是否一样
     */
    fun conventKey(intervalDeal: Float, animPath: AnimPathObject): String {
        val startPoints = animPath.startPoints.toMutableMap()
        val endPointsMap = animPath.animPathMap.toMutableMap()
        val displayItemsMap = animPath.displayItemsMap.values
        var key = "$intervalDeal"
        startPoints.map {
            key += "_"
            key += it.value.map {
                "start_${it.alpha}_${it.point.x}_${it.point.y}_${it.rotation}_${it.scaleX}_${
                    it.scaleY
                }_${it.interpolator.javaClass.name}"
            }
        }
        endPointsMap.map {
            key += "_"
            key += it.value.map {
                val pathObject = it.pathObject
                "end_${pathObject.alpha}_${pathObject.point.x}_${pathObject.point.y}_" +
                        "${pathObject.rotation}_${pathObject.scaleX}_${pathObject.scaleY}" +
                        "_${
                            pathObject.interpolator
                                .javaClass.name
                        }"
            }
        }
        key += "_${
            displayItemsMap.map {
                "${it::class.simpleName}_${it.isCalculate}"
            }
        }"
        val digest = md.digest(key.toByteArray())
        return digest.joinToString("")
    }
}