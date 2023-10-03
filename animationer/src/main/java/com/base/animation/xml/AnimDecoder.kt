package com.base.animation.xml

import android.graphics.Bitmap
import com.base.animation.DisplayObject
import com.base.animation.IAnimView
import com.base.animation.item.BitmapDisplayItem
import com.base.animation.model.AnimPathObject
import com.base.animation.model.PathObject
import com.base.animation.node.AnimNode
import com.base.animation.node.EndNode
import com.base.animation.node.EndNodeContainer
import com.base.animation.node.StartNode
import com.base.animation.xml.node.coder.IAttributeCoder

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
        val displayObject = DisplayObject.with(animView = anim)
        dealStarAnim(key, anim, animNode, displayObject, delegateBitmap)
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
                        kClass = BitmapDisplayItem::class,
                        roomView = anim.getView()
                    ) {
                        val bitmap = delegateBitmap.invoke(it.url)
                        val bitmapWidth = bitmap.width
                        val bitmapHeight = bitmap.height
                        val displayWidth = it.displayHeightSize * bitmapWidth / bitmapHeight
                        BitmapDisplayItem.of(bitmap).apply {
                            if (displayWidth != 0) {
                                setDisplaySize(displayWidth, it.displayHeightSize)
                            }
                        }
                    }

                    val start = it.decode(id, anim)
                    val path = AnimPathObject.Inner.with(
                        displayObject.build(), true
                    ).beginAnimPath(start)
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
                    anim.addAnimDisplay(path.build())
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
        val displayObject = DisplayObject.with(animView = anim)
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