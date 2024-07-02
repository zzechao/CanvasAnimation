package com.base.canvasanimation

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
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.AnimNodeChain
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import kotlin.math.sin
import kotlin.reflect.KClass

class ImageDouNode : ImageNode(), IXmlDrawableNodeDealIntercept {

    @AnimAttributeName("rocation", DefaultAttributeCoder::class)
    @JvmField
    var rocation = 5

    override var displayItem: KClass<out BaseDisplayItem> = BitmapDouDisplay::class

    override val dealIntercept: IDealNodeDealIntercept = object : IDealNodeDealIntercept {
        override suspend fun invoke(
            displayObject: DisplayObject,
            animNode: IAnimNode,
            chain: AnimNodeChain,
            dealDisplayItem: DealDisplayItem
        ): String {
            if (animNode is ImageDouNode) {
                val key =
                    animNode.url + animNode.displayHeightSize + animNode.nodeName
                val bitmapKey = animNode.url + animNode.displayHeightSize + animNode.nodeName
                val displayId = displayObject.suspendAdd(
                    key = key, kClass = animNode.displayItem
                ) {
                    val bitmapDisplayItem = BitmapDouDisplay(rocation)
                    dealDisplayItem.invoke(
                        animNode, bitmapDisplayItem
                    ) // 代理出去处理图片的加载方式
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

    inner class BitmapDouDisplay(val rocation: Int) : BitmapDisplayItem() {

        override var isCalculate: Boolean = true

        override fun calculate(pathProcess: PathProcess, current: AnimDrawObject, interpolator: BaseInterpolator) {
            super.calculate(pathProcess, current, interpolator)
            val p = pathProcess.curTotalTime / pathProcess.durTime
            val interP = pathProcess.interpolator.getInterpolation(p)
            val inPoint = PointF(
                pathProcess.start.point.x + pathProcess.item.totalX * interP,
                pathProcess.start.point.y + pathProcess.item.totalY * interP
            )
            val alpha = pathProcess.start.alpha + (pathProcess.item.totalAlpha * interP).toInt()
            val scaleX = pathProcess.start.scaleX + pathProcess.item.totalScaleX * interP
            val scaleY = pathProcess.start.scaleY + pathProcess.item.totalScaleY * interP
            val rotation = sin(pathProcess.curTotalTime / 30L) * rocation
            current.reset(inPoint, alpha, scaleX, scaleY, rotation)
        }
    }
}

fun AnimEncoder.imageDouNode(onInit: ImageDouNode.(encoder: AnimEncoder) -> Unit) {
    curNode.addNode(ImageDouNode().apply {
        val lastNode = curNode
        try {
            curNode = this
            onInit(this, this@imageDouNode)
        } finally {
            curNode = lastNode
        }
    })
}