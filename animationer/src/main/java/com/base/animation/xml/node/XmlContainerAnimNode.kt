package com.base.animation.xml.node

open class XmlContainerAnimNode<T : XmlBaseAnimNode> : XmlBaseAnimNode {
    private val childNodes by lazy(LazyThreadSafetyMode.NONE) {
        mutableListOf<T>()
    }

    override fun setAttribute(name: String, value: String) {
    }

    override fun createChildNode(name: String): XmlBaseAnimNode? {
        return null
    }

    @Suppress("UNCHECKED_CAST")
    override fun addNode(obj: XmlBaseAnimNode) {
        childNodes.add(obj as T)
    }

    override fun setText(text: String) {
    }
}