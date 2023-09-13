package com.base.animation.node

import com.base.animation.xml.node.AnimNodeName

@AnimNodeName("endContainer")
class EndNodeContainer : IAnimNode {
    private val childNodes by lazy(LazyThreadSafetyMode.NONE) {
        mutableListOf<IAnimNode>()
    }

    override fun addNode(obj: IAnimNode) {
        childNodes.add(obj)
    }

    override fun getNodes(): MutableList<IAnimNode> {
        return childNodes
    }
}