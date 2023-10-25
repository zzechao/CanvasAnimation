package com.base.animation.node

import android.graphics.Color
import com.base.animation.item.BaseDisplayItem
import com.base.animation.item.StringDisplayItem
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.AnimNodeName
import com.base.animation.xml.node.coder.ColorAttributeCoder
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import com.base.animation.xml.node.coder.UrlAttributeCoder
import kotlin.reflect.KClass

/**
 * @author:zhouzechao
 * description：*
 */
@AnimNodeName("txtNode")
class TextNode : IXmlDrawableNode() {

    @AnimAttributeName("txt", UrlAttributeCoder::class)
    @JvmField
    var txt: String = ""

    @AnimAttributeName("txtColor", ColorAttributeCoder::class)
    @JvmField
    var color: Int = Color.BLACK

    @AnimAttributeName("fontSize", DefaultAttributeCoder::class)
    @JvmField
    var fontSize = 10

    override var displayItem: KClass<out BaseDisplayItem> = StringDisplayItem::class
}