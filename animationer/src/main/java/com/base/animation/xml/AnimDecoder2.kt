package com.base.animation.xml

import android.util.Log
import com.base.animation.AnimationEx
import com.base.animation.DisplayObject
import com.base.animation.IAnimView
import com.base.animation.getFragmentActivity
import com.base.animation.item.BitmapDisplayItem
import com.base.animation.item.LayoutDisplayItem
import com.base.animation.item.StringDisplayItem
import com.base.animation.model.AnimPathObject
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

object AnimDecoder2 {

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
        animNode.getNodes().forEach {
            val chain = AnimNodeChain(anim)
            val path = AnimPathObject.Inner.with()
            chain.path = path
            dealAnim(displayObject, it, chain, dealDisplayItem)

            val data = displayObject.build()
            Log.i(
                "zzc", "${
                    data.associateBy {
                        it.displayItemId
                    }
                }"
            )
            chain.anim.addAnimDisplay(path.build(data))
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
                Log.i("zzc", "ImageNode $displayId")
                animNode.getNodes().forEach {
                    if (it is EndNode || it is EndNodeContainer || it is StartNode) {
                        dealAnim(displayObject, it, chain, dealDisplayItem)
                    }
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
                        StringDisplayItem(animNode.fontSize, animNode.txt, animNode.color)
                    dealDisplayItem.invoke(
                        animNode,
                        stringDisplayItem
                    )
                }
                if (displayId.isNotEmpty()) {
                    chain.curDisplayId = displayId
                }
                Log.i("zzc", "TextNode $displayId")
                animNode.getNodes().forEach {
                    if (it is EndNode || it is EndNodeContainer || it is StartNode) {
                        dealAnim(displayObject, it, chain, dealDisplayItem)
                    }
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
                Log.i("zzc", "LayoutNode $displayId")
                animNode.getNodes().forEach {
                    if (it is EndNode || it is EndNodeContainer || it is StartNode) {
                        dealAnim(displayObject, it, chain, dealDisplayItem)
                    }
                }
            }

            is StartNode -> {
                if (animNode.point != null && animNode.getNodes().isNotEmpty()) {
                    val start = animNode.decode(chain.curDisplayId, chain.anim)
                    if (chain.isBeginPointSet) {
                        chain.nextStart = start
                    } else {
                        chain.isBeginPointSet = true
                        chain.path?.beginAnimPath(start)
                    }
                    val displayObjectId = chain.curDisplayId
                    animNode.getNodes().forEach {
                        if (it is EndNode) {
                            val next = if (displayObjectId.isNotEmpty()) {
                                Log.i("zzc", "StartNode EndNode $displayObjectId")
                                it.decode(displayObjectId, chain.anim)
                            } else {
                                Log.i("zzc", "StartNode EndNode2 ${chain.curDisplayId}")
                                it.decode(chain.curDisplayId, chain.anim)
                            }
                            chain.nextStart?.let {
                                it.displayItemId = next.displayItemId
                                chain.path?.beginNextAnimPath(it)
                            }
                            chain.path?.doAnimPath(it.durTime, next)
                            chain.nextStart = next
                        } else if (it is EndNodeContainer) {
                            dealAnim(displayObject, it, chain, dealDisplayItem)
                        } else if (it is IXmlDrawableNode) {
                            dealAnim(displayObject, it, chain, dealDisplayItem)
                        }
                    }
                }
            }

            is EndNode -> {
                Log.i("zzc", "EndNode ${chain.curDisplayId}")
                val next = animNode.decode(chain.curDisplayId, chain.anim)
                chain.nextStart?.let {
                    chain.nextStart?.displayItemId = next.displayItemId
                    chain.path?.beginNextAnimPath(it)
                }
                chain.path?.doAnimPath(animNode.durTime, next)
                chain.nextStart = next
            }

            is EndNodeContainer -> {
                val nextList = animNode.getNodes().map {
                    it.decode(chain.curDisplayId, chain.anim)
                }.filterNotNull()
                chain.nextStart?.let {
                    chain.path?.beginNextAnimPath(it)
                }
                chain.path?.doAnimPaths(animNode.durTime, nextList)
            }
        }
    }
}