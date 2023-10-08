package com.base.animation.node

import com.base.animation.item.BaseDisplayItem
import com.base.animation.xml.XmlWriterHelper
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import kotlin.reflect.KClass

open class IXmlDrawableNode : IAnimNode {

    open var displayItem: KClass<out BaseDisplayItem> = BaseDisplayItem::class

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