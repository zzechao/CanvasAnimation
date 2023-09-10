package com.base.animation.xml.node.coder

import kotlin.reflect.KClass

class LocationAttributeCoder : IAttributeCoder<String> {
    override fun attributeDecode(needType: KClass<*>, value: String): Any? {
        return if (needType == Location::class) {
            return ValueLoader.fromObject(value, Location::class.java)
        } else {
            null
        }
    }

    override fun attributeEncode(value: Any?): String? {
        return value?.toString()
    }
}

data class Location(val x: Float, val y: Float)