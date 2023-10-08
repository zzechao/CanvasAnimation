package com.base.animation.node

import com.base.animation.item.BaseDisplayItem
import com.base.animation.item.BitmapDisplayItem
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.AnimNodeName
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import com.base.animation.xml.node.coder.UrlAttributeCoder
import kotlin.reflect.KClass


@AnimNodeName(name = "imageNode")
class ImageNode : IXmlDrawableNode() {

    @AnimAttributeName("url", UrlAttributeCoder::class)
    @JvmField
    var url: String = ""

    @AnimAttributeName("displaySize", DefaultAttributeCoder::class)
    @JvmField
    var displayHeightSize = 80

    override var displayItem: KClass<out BaseDisplayItem> = BitmapDisplayItem::class
}