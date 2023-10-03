package com.base.animation.xml.node.coder

import kotlin.reflect.KClass

class LayoutIDAttributeCoder : IAttributeCoder<String> {
    override fun attributeDecode(needType: KClass<*>, value: String): Any? {
        return if (needType == String::class) {
            return value
        } else {
            null
        }
    }

    override fun attributeEncode(value: Any?): String? {
        return value?.toString()
    }
}