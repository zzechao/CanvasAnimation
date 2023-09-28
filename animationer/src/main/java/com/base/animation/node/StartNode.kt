package com.base.animation.node

import android.graphics.PointF
import android.util.Log
import androidx.annotation.CallSuper
import com.base.animation.model.PathObject
import com.base.animation.xml.XmlWriterHelper
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.AnimNodeName
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import com.base.animation.xml.node.coder.IAttributeCoder
import com.base.animation.xml.node.coder.LayoutIDAttributeCoder
import com.base.animation.xml.node.coder.LocationAttributeCoder
import com.base.animation.xml.node.coder.UrlAttributeCoder
import java.lang.reflect.Field

@AnimNodeName("startAnim")
class StartNode : IAnimNode {

    @AnimAttributeName("startL", LocationAttributeCoder::class)
    @JvmField
    var point: PointF? = null

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

    @AnimAttributeName("alpha")
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
        return PathObject(id, point ?: PointF(), alpha, scaleX, scaleY, rotation)
    }
}