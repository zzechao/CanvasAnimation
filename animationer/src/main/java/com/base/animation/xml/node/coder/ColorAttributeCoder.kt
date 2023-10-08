package com.base.animation.xml.node.coder

import android.graphics.Color
import kotlin.reflect.KClass


class ColorAttributeCoder : IAttributeCoder<String> {
    override fun attributeDecode(needType: KClass<*>, value: String): Any {
        return if (needType == String::class) {
            Color.parseColor(value)
        } else {
            ""
        }
    }

    override fun attributeEncode(value: Any?): String? {
        return if (value is Int) {
            Integer.toHexString(value and 0x00FFFFFF)
        } else {
            null
        }
    }
}