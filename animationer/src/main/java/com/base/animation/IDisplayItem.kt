package com.base.animation

import android.graphics.Canvas
import android.graphics.PointF
import com.base.animation.model.AnimDrawObject

/**
 * @author:zhouzechao
 * @date: 2020/12/4
 * description：播放每个视图
 */
interface IDisplayItem {

    var width: Int

    var height: Int

    var displayItemId: String

    /**
     * 接触view的大小后
     */
    fun attachView(width: Int, height: Int)

    fun getScalePX(scaleX: Float): Float

    fun getScalePY(scaleY: Float): Float

    fun getRotatePX(rotation: Float, scaleX: Float): Float

    fun getRotatePY(rotation: Float, scaleY: Float): Float

    fun setDisplaySize(displayWidth: Int, displayHeight: Int)

    /**
     * 绘制
     */
    fun draw(
        canvas: Canvas, x: Float, y: Float, alpha: Int,
        scaleX: Float, scaleY: Float, rotation: Float
    )

    /**
     * 点击位置，在Touch的ACTION_UP返回
     */
    fun touch(
        animId: Long,
        iClickIntercepts: MutableList<IClickIntercept>,
        animDrawObject: AnimDrawObject,
        touchPoint: MutableList<PointF>
    )
}