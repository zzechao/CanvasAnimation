package com.base.canvasanimation

import android.animation.TypeEvaluator
import android.graphics.PointF
import android.view.animation.BaseInterpolator
import com.base.animation.DisplayObject
import com.base.animation.helper.data.PathProcess
import com.base.animation.item.BaseDisplayItem
import com.base.animation.item.BitmapDisplayItem
import com.base.animation.model.AnimDrawObject
import com.base.animation.node.IAnimNode
import com.base.animation.node.IXmlDrawableNodeDealIntercept
import com.base.animation.node.ImageNode
import com.base.animation.xml.AnimEncoder
import com.base.animation.xml.DealDisplayItem
import com.base.animation.xml.IDealNodeDealIntercept
import com.base.animation.xml.node.AnimNodeChain
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/7/1 14:46
 * description：抛物线
 */
class ImageBezierNode : ImageNode(), IXmlDrawableNodeDealIntercept {

    override var displayItem: KClass<out BaseDisplayItem> = BezierDisplayItem::class

    override val dealIntercept: IDealNodeDealIntercept = object : IDealNodeDealIntercept {
        override suspend fun invoke(
            displayObject: DisplayObject,
            animNode: IAnimNode,
            chain: AnimNodeChain,
            dealDisplayItem: DealDisplayItem
        ): String {
            if (animNode is ImageBezierNode) {
                val key =
                    animNode.url + animNode.displayHeightSize + animNode.nodeName + System.currentTimeMillis()
                val displayId = displayObject.suspendAdd(
                    key = key, kClass = animNode.displayItem
                ) {
                    val bitmapDisplayItem = BezierDisplayItem()
                    dealDisplayItem.invoke(animNode, bitmapDisplayItem) // 代理出去处理图片的加载方式
                    val bitmapWidth = bitmapDisplayItem.mBitmap?.width ?: return@suspendAdd null
                    val bitmapHeight = bitmapDisplayItem.mBitmap?.height ?: return@suspendAdd null
                    val displayWidth = animNode.displayHeightSize * bitmapWidth / bitmapHeight
                    bitmapDisplayItem.setDisplaySize(displayWidth, animNode.displayHeightSize)
                    bitmapDisplayItem
                }
                return displayId
            }
            return ""
        }
    }

    /**
     * 贝塞尔绘制
     */
    inner class BezierDisplayItem : BitmapDisplayItem(), TypeEvaluator<PointF> {
        override var isCalculate: Boolean = true

        private var controlPointF: PointF? = null

        override fun calculate(pathProcess: PathProcess, current: AnimDrawObject, interpolator: BaseInterpolator) {
            super.calculate(pathProcess, current, interpolator)
            val controlPointFX = (pathProcess.end.point.x + pathProcess.start.point.x) / 2
            val controlPointFY = pathProcess.start.point.y
            controlPointF = PointF(controlPointFX, controlPointFY)
            val p = pathProcess.curTotalTime / pathProcess.durTime
            val interP = pathProcess.interpolator.getInterpolation(p)
            val inPoint = evaluate(interP, pathProcess.start.point, pathProcess.end.point)
            val alpha = pathProcess.start.alpha + (pathProcess.item.totalAlpha * interP).toInt()
            val scaleX = pathProcess.start.scaleX + pathProcess.item.totalScaleX * interP
            val scaleY = pathProcess.start.scaleY + pathProcess.item.totalScaleY * interP
            val rotation = pathProcess.start.rotation + pathProcess.item.totalRotation * interP
            current.reset(inPoint, alpha, scaleX, scaleY, rotation)
        }

        override fun evaluate(t: Float, startValue: PointF, endValue: PointF): PointF {
            val totalX = endValue.x - startValue.x
            val totalY = endValue.y - startValue.y
            return controlPointF?.let {
                val x = (1 - t) * (1 - t) * startValue.x + 2 * t * (1 - t) * it.x + t * t * endValue.x
                val y = (1 - t) * (1 - t) * startValue.y + 2 * t * (1 - t) * it.y + t * t * endValue.y
                PointF(x, y)
            } ?: PointF(startValue.x + totalX * t, startValue.y + totalY * t)
        }
    }
}

/**
 * 贝塞尔
 */
fun AnimEncoder.imageBezierNode(onInit: ImageBezierNode.(encoder: AnimEncoder) -> Unit) {
    curNode.addNode(ImageBezierNode().apply {
        val lastNode = curNode
        try {
            curNode = this
            onInit(this, this@imageBezierNode)
        } finally {
            curNode = lastNode
        }
    })
}