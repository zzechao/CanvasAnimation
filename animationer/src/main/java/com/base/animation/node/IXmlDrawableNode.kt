package com.base.animation.node

import com.base.animation.item.BaseDisplayItem
import com.base.animation.xml.IDealNodeDealIntercept
import com.base.animation.xml.XmlWriterHelper
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import kotlin.reflect.KClass

/**
 * @author:zhouzechao
 * description：自定义节点处理处理特殊的动画信息和对应的DisplayItem的绘制信息
 */
open class IXmlDrawableNode : IAnimNode {

    @AnimAttributeName("clickable", DefaultAttributeCoder::class)
    @JvmField
    var clickable: Boolean = false

    @AnimAttributeName("extras", DefaultAttributeCoder::class)
    @JvmField
    var extras: String = ""

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

interface IXmlDrawableNodeDealIntercept {
    val dealIntercept: IDealNodeDealIntercept
}