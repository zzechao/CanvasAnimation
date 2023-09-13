package com.base.animation.xml

import android.graphics.Bitmap
import com.base.animation.DisplayObject
import com.base.animation.IAnimView
import com.base.animation.item.BitmapDisplayItem
import com.base.animation.model.AnimPathObject
import com.base.animation.model.PathObject
import com.base.animation.node.AnimNode
import com.base.animation.node.EndNode
import com.base.animation.node.StartNode

object AnimDecoder {

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
                if (it.point.isNotBlank() && it.getNodes().isNotEmpty()) {
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
                            setDisplaySize(displayWidth, it.displayHeightSize)
                        }
                    }

                    val start = it.decode(id)
                    val path = AnimPathObject.Inner.with(
                        displayObject.build(), true
                    ).beginAnimPath(start)
                    var preNext: PathObject? = null
                    it.getNodes().forEach {
                        if (it is EndNode) {
                            val next = it.decode(id)
                            preNext?.let {
                                path.beginNextAnimPath(it)
                            }
                            path.doAnimPath(it.durTime, next)
                            preNext = next
                        }
                    }
                    anim.addAnimDisplay(path.build())
                }
            }
        }
    }
}