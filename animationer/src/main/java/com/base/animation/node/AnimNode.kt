package com.base.animation.node

import com.base.animation.xml.XmlWriterHelper
import com.base.animation.xml.node.AnimNodeName

/**
 * @author:zhouzechao
 * description：*
 */
@AnimNodeName(name = "anim")
class AnimNode : IAnimNode {
    
    private val childNodes by lazy(LazyThreadSafetyMode.NONE) {
        mutableListOf<IAnimNode>()
    }

    override fun addNode(obj: IAnimNode) {
        childNodes.add(obj)
    }

    override fun encodeSelf(write: XmlWriterHelper) {
        childNodes.forEach {
            it.encode(write)
        }
    }

    override fun getNodes(): MutableList<IAnimNode> {
        return childNodes
    }
}