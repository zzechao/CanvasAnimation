package com.base.animation.node

import com.base.animation.xml.XmlWriterHelper
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.AnimNodeName
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import com.base.animation.xml.node.coder.UrlAttributeCoder

/**
 * @author:zhouzechao
 * description：*
 */
@AnimNodeName("endContainer")
class EndNodeContainer : IAnimNode {

    @AnimAttributeName("durTime", DefaultAttributeCoder::class)
    @JvmField
    var durTime: Long = 0

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