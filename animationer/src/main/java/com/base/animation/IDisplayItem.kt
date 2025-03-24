package com.base.animation

import android.graphics.Canvas
import android.graphics.PointF
import android.view.animation.BaseInterpolator
import com.base.animation.gles.EGLRender
import com.base.animation.helper.data.PathProcess
import com.base.animation.model.AnimDrawObject

/**
 * @author:zhouzechao
 * @date: 2020/12/4
 * description：播放每个视图
 */
interface IDisplayItem {

    var displayItemId: String

    var isCalculate: Boolean

    fun getScalePX(scaleX: Float): Float

    fun getScalePY(scaleY: Float): Float

    fun getRotatePX(rotation: Float, scaleX: Float): Float

    fun getRotatePY(rotation: Float, scaleY: Float): Float

    fun setDisplaySize(displayWidth: Int, displayHeight: Int)

    /**
     * 是否自己进行绘制坐标的计算，只能针对com.base.animation.AnimationEx.getMode为2时使用
     */
    fun calculate(pathProcess: PathProcess, current: AnimDrawObject, interpolator: BaseInterpolator) {}

    /**
     * 绘制
     */
    fun draw(canvas: Canvas, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float, rotation: Float)

    fun drawRender(animId: Long, render: EGLRender, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float, rotation: Float)

    /**
     * 点击位置，在Touch的ACTION_UP返回
     */
    fun touch(animId: Long, onAnimItemClick: OnAnimItemClick, animDrawObject: AnimDrawObject, touchPoint: DoubleLinkedReference<PointF>, extra: String)
}