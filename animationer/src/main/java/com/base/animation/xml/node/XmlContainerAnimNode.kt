package com.base.animation.xml.node

import com.base.animation.node.IAnimNode

/**
 * @author:zhouzechao
 * description：*
 */
open class XmlContainerAnimNode<T : XmlBaseAnimNode> : XmlBaseAnimNode {
    private val childNodes by lazy(LazyThreadSafetyMode.NONE) {
        mutableListOf<T>()
    }

    override fun setAttribute(name: String, value: String) {
    }

    override fun createChildNode(name: String): IAnimNode? {
        return null
    }

    @Suppress("UNCHECKED_CAST")
    override fun addNode(obj: IAnimNode) {
        childNodes.add(obj as T)
    }

    override fun setText(text: String) {
    }
}