package com.base.animation.node

import com.base.animation.item.BaseDisplayItem
import com.base.animation.item.LayoutDisplayItem
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.AnimNodeName
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import com.base.animation.xml.node.coder.UrlAttributeCoder
import kotlin.reflect.KClass

/**
 * @author:zhouzechao
 * description：*
 */
@AnimNodeName(name = "layoutNode")
class LayoutNode : IXmlDrawableNode() {

    @AnimAttributeName("layoutIdName", UrlAttributeCoder::class)
    @JvmField
    var layoutIdName: String = ""

    /**
     * 布局修改后更新版本号
     */
    @AnimAttributeName("versionCode", DefaultAttributeCoder::class)
    @JvmField
    var versionCode: String = "version_1.0"

    /**
     * 识别码，数据复用要用到（保证唯一，不同数据唯一性, 例如{"url":xxx,txt:xxx}），
     * layout的布局要预埋，旧版找不到不会显示或者用上一个displayItem显示剩余动画
     */
    @AnimAttributeName("data", DefaultAttributeCoder::class)
    @JvmField
    var data: String = ""

    override var displayItem: KClass<out BaseDisplayItem> = LayoutDisplayItem::class
}