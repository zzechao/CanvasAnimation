package com.base.animation.xml.node.coder

import android.graphics.PointF
import kotlin.reflect.KClass

class LocationAttributeCoder : IAttributeCoder<String> {
    override fun attributeDecode(needType: KClass<*>, value: String): Any? {
        return if (needType == PointF::class) {
            return ValueLoader.fromObject<PointF>(value, PointF::class.java)
        } else {
            null
        }
    }

    override fun attributeEncode(value: Any?): String? {
        return value?.let { ValueLoader.toJsonString(it) }
    }
}

data class Location(val x: Float, val y: Float)