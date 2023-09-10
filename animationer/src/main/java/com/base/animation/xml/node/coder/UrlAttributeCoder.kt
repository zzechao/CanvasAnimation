package com.base.animation.xml.node.coder

import java.net.URLDecoder
import kotlin.reflect.KClass

class UrlAttributeCoder : IAttributeCoder<String> {
    override fun attributeDecode(needType: KClass<*>, value: String): Any? {
        return if (needType == String::class) {
            URLDecoder.decode(value, "utf-8")
        } else {
            null
        }
    }

    override fun attributeEncode(value: Any?): String? {
        return value?.toString()
    }
}