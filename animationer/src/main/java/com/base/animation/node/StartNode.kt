package com.base.animation.node

import android.graphics.PointF
import com.base.animation.IAnimView
import com.base.animation.model.PathObject
import com.base.animation.xml.XmlWriterHelper
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.AnimNodeName
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import com.base.animation.xml.node.coder.LayoutIDAttributeCoder
import com.base.animation.xml.node.coder.LocationAttributeCoder
import com.base.animation.xml.node.coder.UrlAttributeCoder

@AnimNodeName("startAnim")
class StartNode : IAnimNode {

    @AnimAttributeName("startL", LocationAttributeCoder::class)
    @JvmField
    var point: PointF? = null

    @AnimAttributeName("startIdName", LayoutIDAttributeCoder::class)
    @JvmField
    var layoutIdName: String = ""

    @AnimAttributeName("scaleX", DefaultAttributeCoder::class)
    @JvmField
    var scaleX = 1f

    @AnimAttributeName("scaleY", DefaultAttributeCoder::class)
    @JvmField
    var scaleY = 1f

    @AnimAttributeName("alpha")
    @JvmField
    var alpha = 255

    @AnimAttributeName("rotation", DefaultAttributeCoder::class)
    @JvmField
    var rotation = 0f

    private val childNodes by lazy(LazyThreadSafetyMode.NONE) {
        mutableListOf<IAnimNode>()
    }

    override fun addNode(obj: IAnimNode) {
        childNodes.add(obj)
    }

    override fun encodeSelf(write: XmlWriterHelper) {
        childNodes.forEach { node ->
            node.encode(write)
        }
    }

    override fun getNodes(): MutableList<IAnimNode> {
        return childNodes
    }

    override fun decode(id: String, anim: IAnimView): PathObject {
        val pointF = if ((point?.x ?: 0f) == 0f && (point?.y ?: 0f) == 0f
            && layoutIdName.isNotEmpty()
        ) {
            anim.getViewByAnimName(layoutIdName)?.let { it ->
                getCenterOfViewLocationInWindow(it).let {
                    PointF(it[0].toFloat(), it[1].toFloat())
                }
            } ?: PointF()
        } else {
            point ?: PointF()
        }
        return PathObject(id, pointF, alpha, scaleX, scaleY, rotation)
    }
}