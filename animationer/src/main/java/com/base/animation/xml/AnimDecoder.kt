package com.base.animation.xml

import com.base.animation.DisplayObject
import com.base.animation.IAnimView
import com.base.animation.item.BitmapDisplayItem
import com.base.animation.model.AnimPathObject
import com.base.animation.model.PathObject
import com.base.animation.node.AnimNode
import com.base.animation.node.EndNode
import com.base.animation.node.StartNode

object AnimDecoder {

    fun playAnimWithNode(anim: IAnimView, animNode: AnimNode, create: () -> BitmapDisplayItem) {
        val displayObject = DisplayObject.with(animView = anim)
        val displayItemId1 = displayObject.add(
            key = "xin_startSingleAnim",
            kClass = BitmapDisplayItem::class,
            roomView = anim.getView()
        ) {
            return@add create()
        }
        dealStarAnim(anim, animNode, displayItemId1, displayObject)
    }

    private fun dealStarAnim(
        anim: IAnimView,
        animNode: AnimNode,
        id: String,
        displayObject: DisplayObject
    ) {
        animNode.getNodes().forEach {
            if (it is StartNode) {
                if (it.point.isNotBlank() && it.getNodes().isNotEmpty()) {
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