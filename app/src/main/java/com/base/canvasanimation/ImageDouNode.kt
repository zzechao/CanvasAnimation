package com.base.canvasanimation

import com.base.animation.DisplayObject
import com.base.animation.item.BaseDisplayItem
import com.base.animation.node.IAnimNode
import com.base.animation.node.IXmlDrawableNodeDealIntercept
import com.base.animation.node.ImageNode
import com.base.animation.xml.DealDisplayItem
import com.base.animation.xml.IDealNodeDealIntercept
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.AnimNodeChain
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import kotlin.reflect.KClass

class ImageDouNode : ImageNode(), IXmlDrawableNodeDealIntercept {

    @AnimAttributeName("rocation", DefaultAttributeCoder::class)
    @JvmField
    var rocation = 10

    override var displayItem: KClass<out BaseDisplayItem> = BitmapDouDisplay::class

    override val dealIntercept: IDealNodeDealIntercept = object : IDealNodeDealIntercept {
        override suspend fun invoke(
            displayObject: DisplayObject,
            animNode: IAnimNode,
            chain: AnimNodeChain,
            dealDisplayItem: DealDisplayItem
        ): String {
            if (animNode is ImageDouNode) {
                val key = animNode.url + animNode.displayHeightSize + animNode.nodeName
                val displayId = displayObject.suspendAdd(
                    key = key,
                    kClass = animNode.displayItem
                ) {
                    val bitmapDisplayItem = BitmapDouDisplay()
                    dealDisplayItem.invoke(
                        animNode,
                        bitmapDisplayItem
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
}