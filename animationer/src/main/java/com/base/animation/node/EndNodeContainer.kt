package com.base.animation.node

import com.base.animation.xml.XmlWriterHelper
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.AnimNodeName
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import com.base.animation.xml.node.coder.UrlAttributeCoder

@AnimNodeName("endContainer")
class EndNodeContainer : IAnimNode {

    @AnimAttributeName("url", UrlAttributeCoder::class)
    @JvmField
    var url: String = ""

    @AnimAttributeName("displaySize", DefaultAttributeCoder::class)
    @JvmField
    var displayHeightSize = 0

    @AnimAttributeName("durTime", DefaultAttributeCoder::class)
    @JvmField
    var durTime: Long = 1000

    private val childNodes by lazy(LazyThreadSafetyMode.NONE) {
        mutableListOf<IAnimNode>()
    }

    override fun addNode(obj: IAnimNode) {
        childNodes.add(obj)
    }

    override fun getNodes(): MutableList<IAnimNode> {
        return childNodes
    }

    override fun encodeSelf(write: XmlWriterHelper) {
        childNodes.forEach {
            it.encode(write)
        }
    }
}