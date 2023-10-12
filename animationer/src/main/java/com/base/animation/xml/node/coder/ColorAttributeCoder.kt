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
            var alpha = Integer.toHexString(Color.alpha(value))
            var red = Integer.toHexString(Color.red(value))
            var green = Integer.toHexString(Color.green(value))
            var blue = Integer.toHexString(Color.blue(value))
            if (alpha.length == 1) alpha = "0$alpha"
            if (red.length == 1) red = "0$red"
            if (green.length == 1) green = "0$green"
            if (blue.length == 1) blue = "0$blue"
            return "#$alpha$red$green$blue"
        } else {
            null
        }
    }
}