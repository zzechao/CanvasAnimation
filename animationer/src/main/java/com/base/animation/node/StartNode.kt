package com.base.animation.node

import android.graphics.PointF
import com.base.animation.model.PathObject
import com.base.animation.xml.XmlWriterHelper
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.AnimNodeName
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import com.base.animation.xml.node.coder.LayoutIDAttributeCoder
import com.base.animation.xml.node.coder.LocationAttributeCoder
import com.base.animation.xml.node.coder.UrlAttributeCoder
import com.base.animation.xml.node.coder.ValueLoader

@AnimNodeName("startAnim")
class StartNode : IAnimNode {

    @AnimAttributeName("startL", LocationAttributeCoder::class)
    @JvmField
    var point: String = ""

    @AnimAttributeName("startId", LayoutIDAttributeCoder::class)
    @JvmField
    var layoutId: Long = 0

    @AnimAttributeName("url", UrlAttributeCoder::class)
    @JvmField
    var url: String = ""

    @AnimAttributeName("scaleX", DefaultAttributeCoder::class)
    @JvmField
    var scaleX = 1f

    @AnimAttributeName("scaleY", DefaultAttributeCoder::class)
    @JvmField
    var scaleY = 1f

    @AnimAttributeName("")
    @JvmField
    var alpha = 255

    @AnimAttributeName("rotation", DefaultAttributeCoder::class)
    @JvmField
    var rotation = 0f

    @AnimAttributeName("displaySize", DefaultAttributeCoder::class)
    @JvmField
    var displayHeightSize = 80

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

    override fun decode(id: String): PathObject {
        val pointF = ValueLoader.fromObject<PointF>(point, PointF::class.java)
        return PathObject(id, pointF, alpha, scaleX, scaleY, rotation)
    }
}