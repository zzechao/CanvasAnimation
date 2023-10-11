package com.base.animation.xml

import android.graphics.Bitmap
import android.graphics.Color
import com.base.animation.AnimationEx
import com.base.animation.DisplayObject
import com.base.animation.IAnimView
import com.base.animation.getFragmentActivity
import com.base.animation.item.BaseDisplayItem
import com.base.animation.item.BitmapDisplayItem
import com.base.animation.item.LayoutDisplayItem
import com.base.animation.item.StringDisplayItem
import com.base.animation.model.AnimPathObject
import com.base.animation.model.PathObject
import com.base.animation.node.AnimNode
import com.base.animation.node.EndNode
import com.base.animation.node.EndNodeContainer
import com.base.animation.node.IAnimNode
import com.base.animation.node.IXmlDrawableNode
import com.base.animation.node.ImageNode
import com.base.animation.node.LayoutNode
import com.base.animation.node.StartNode
import com.base.animation.node.TextNode
import com.base.animation.xml.node.AnimNodeChain
import com.base.animation.xml.node.coder.IAttributeCoder


/**
 * 数据回调进行上层自定义处理
 */
typealias DealDisplayItem = suspend (
    IXmlDrawableNode,
    BaseDisplayItem
) -> BaseDisplayItem

object AnimDecoder {

    private val decoder by lazy {
        XmlObjectDecoder().apply {
            registerNodeCreatetor(AnimNode::class.java)
            registerNodeCreatetor(StartNode::class.java)
            registerNodeCreatetor(EndNode::class.java)
            registerNodeCreatetor(EndNodeContainer::class.java)
        }
    }

    val mapNodeAttributeCoderMap by lazy {
        mutableMapOf<String, IAttributeCoder<out Any>>()
    }

    suspend fun suspendPlayAnimWithNode(
        key: String,
        anim: IAnimView,
        animNode: AnimNode,
        delegateBitmap: suspend (String) -> Bitmap
    ) {
        val displayObject = DisplayObject.with()
        dealStarAnim(key, anim, animNode, displayObject, delegateBitmap)
    }

    suspend fun suspendPlayAnimWithAnimNode(
        anim: IAnimView,
        animNode: AnimNode,
        dealDisplayItem: DealDisplayItem
    ) {
        dealAnim(anim, animNode, dealDisplayItem)
    }

    private suspend fun dealAnim(
        anim: IAnimView,
        animNode: AnimNode,
        dealDisplayItem: DealDisplayItem
    ) {
        val displayObject = DisplayObject.with()
        val chain = AnimNodeChain(anim)
        animNode.getNodes().forEach {
            val path = AnimPathObject.Inner.with()
            dealAnim(displayObject, it, chain, dealDisplayItem)
        }
    }

    private suspend fun dealAnim(
        displayObject: DisplayObject,
        animNode: IAnimNode,
        chain: AnimNodeChain,
        dealDisplayItem: DealDisplayItem
    ) {
        when (animNode) {
            is ImageNode -> {
                if (animNode.getNodes().isEmpty()) return
                val key = animNode.url + animNode.displayHeightSize + animNode.nodeName
                val displayId = displayObject.suspendAdd(
                    key = key,
                    kClass = BitmapDisplayItem::class
                ) {
                    val bitmapDisplayItem = BitmapDisplayItem()
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
                if (displayId.isNotEmpty()) {
                    chain.curDisplayId = displayId
                }
                animNode.getNodes().forEach {
                    dealAnim(displayObject, it, chain, dealDisplayItem)
                }
            }

            is TextNode -> {
                if (animNode.getNodes().isEmpty()) return
                val key = animNode.txt + animNode.fontSize + animNode.color + animNode.nodeName
                val displayId = displayObject.suspendAdd(
                    key = key,
                    kClass = StringDisplayItem::class
                ) {
                    val stringDisplayItem =
                        StringDisplayItem(
                            animNode.fontSize,
                            animNode.txt,
                            animNode.color
                        )
                    dealDisplayItem.invoke(
                        animNode,
                        stringDisplayItem
                    )
                }
                if (displayId.isNotEmpty()) {
                    chain.curDisplayId = displayId
                }
                animNode.getNodes().forEach {
                    dealAnim(displayObject, it, chain, dealDisplayItem)
                }
            }

            is LayoutNode -> {
                if (animNode.getNodes().isEmpty()) return
                val key = animNode.layoutIdName + animNode.data
                val displayId = displayObject.suspendAdd(
                    key = key,
                    kClass = LayoutDisplayItem::class
                ) {
                    val layoutId = AnimationEx.mApplication.resources.getIdentifier(
                        animNode.layoutIdName,
                        "layout",
                        AnimationEx.mApplication.packageName
                    )
                    if (layoutId > 0) {
                        val activity =
                            chain.anim.getView()?.getFragmentActivity() ?: return@suspendAdd null
                        val layoutDisplayItem = LayoutDisplayItem(activity, layoutId)
                        dealDisplayItem.invoke(
                            animNode,
                            layoutDisplayItem
                        )
                        return@suspendAdd layoutDisplayItem
                    } else {
                        return@suspendAdd null
                    }
                }
                if (displayId.isNotEmpty()) {
                    chain.curDisplayId = displayId
                }
                animNode.getNodes().forEach {
                    dealAnim(displayObject, it, chain, dealDisplayItem)
                }
            }

            is StartNode -> {
                if (animNode.point != null && animNode.getNodes().isNotEmpty()) {
                    val start = animNode.decode(chain.curDisplayId, chain.anim)
                    val path = AnimPathObject.Inner.with(
                    ).beginAnimPath(start)
                    var preNext: PathObject? = null
                    animNode.getNodes().forEach {
                        if (it is EndNode) {

                        } else if (it is EndNodeContainer) {

                        } else if (it is IXmlDrawableNode) {

                        }
                    }
                    chain.anim.addAnimDisplay(path.build(displayObject.build()))
                }
            }

            is EndNode -> {

            }

            is EndNodeContainer -> {

            }
        }
    }


    suspend fun suspendPlayViewAnimWithNode(
        key: String,
        anim: IAnimView,
        animNode: AnimNode,
        layout: Int
    ) {
        val activity = anim.getView()?.getFragmentActivity() ?: return
        val displayObject = DisplayObject.with()
        animNode.getNodes().forEach {
            if (it is StartNode) {
                if (it.point != null && it.getNodes().isNotEmpty()) {
                    val id = displayObject.suspendAdd(
                        key = key,
                        kClass = LayoutDisplayItem::class
                    ) {
                        LayoutDisplayItem(activity, layout)
                    }

                    val start = it.decode(id, anim)
                    val path = AnimPathObject.Inner.with().beginAnimPath(start)
                    var preNext: PathObject? = null
                    it.getNodes().forEach {
                        if (it is EndNode) {
                            val next = it.decode(id, anim)
                            preNext?.let {
                                path.beginNextAnimPath(it)
                            }
                            path.doAnimPath(it.durTime, next)
                            preNext = next
                        }
                        if (it is EndNodeContainer) {
                            val nextList = it.getNodes().map {
                                it.decode(id, anim)
                            }.filterNotNull()
                            preNext?.let {
                                path.beginNextAnimPath(it)
                            }
                            path.doAnimPaths(it.durTime, nextList)
                        }
                    }
                    anim.addAnimDisplay(path.build(displayObject.build()))
                }
            }
        }
    }


    private suspend fun dealStarAnim(
        key: String,
        anim: IAnimView,
        animNode: AnimNode,
        displayObject: DisplayObject,
        delegateBitmap: suspend (String) -> Bitmap
    ) {
        animNode.getNodes().forEach {

            if (it is StartNode) {
                if (it.point != null && it.getNodes().isNotEmpty()) {
                    val id = displayObject.suspendAdd(
                        key = key,
                        kClass = StringDisplayItem::class
                    ) {
                        StringDisplayItem(
                            80,
                            "测试数据",
                            Color.BLUE
                        )
                    }

                    val start = it.decode(id, anim)
                    val path = AnimPathObject.Inner.with().beginAnimPath(start)
                    var preNext: PathObject? = null
                    it.getNodes().forEach {
                        if (it is EndNode) {
                            val next = it.decode(id, anim)
                            preNext?.let {
                                path.beginNextAnimPath(it)
                            }
                            path.doAnimPath(it.durTime, next)
                            preNext = next
                        }
                        if (it is EndNodeContainer) {
                            val nextList = it.getNodes().map {
                                it.decode(id, anim)
                            }.filterNotNull()
                            preNext?.let {
                                path.beginNextAnimPath(it)
                            }
                            path.doAnimPaths(it.durTime, nextList)
                        }
                    }
                    anim.addAnimDisplay(path.build(displayObject.build()))
                }
            }
        }
    }

    suspend fun suspendPlayAnimWithXml(
        key: String,
        anim: IAnimView,
        xml: String,
        delegateBitmap: suspend (String) -> Bitmap
    ) {
        val displayObject = DisplayObject.with()
        dealStarAnimXml(key, anim, xml, displayObject, delegateBitmap)
    }

    private suspend fun dealStarAnimXml(
        key: String,
        anim: IAnimView,
        xml: String,
        displayObject: DisplayObject,
        delegateBitmap: suspend (String) -> Bitmap
    ) {
        (decoder.createObject(xml) as? AnimNode)?.let {
            dealStarAnim(key, anim, it, displayObject, delegateBitmap)
        }
    }
}