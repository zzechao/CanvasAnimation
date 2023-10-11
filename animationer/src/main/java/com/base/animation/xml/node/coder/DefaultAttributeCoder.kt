package com.base.animation.xml.node.coder

import android.util.Log
import kotlin.reflect.KClass

class DefaultAttributeCoder : IAttributeCoder<Any> {
    override fun attributeDecode(needType: KClass<*>, value: String): Any? {
        return try {
            when (needType) {
                Int::class -> value.toInt()
                String::class -> value
                Float::class -> value.toFloat()
                Double::class -> value.toDouble()
                Long::class -> value.toLong()
                Boolean::class -> value.equals("true", true)
                else -> null
            }
        } catch (e: Exception) {
            Log.e("DefaultAttributeCoder", "[attributeDecode]:$e")
            null
        }
    }

    override fun attributeEncode(value: Any?): Any? {
        return when (value) {
            is Boolean -> value.toString()
            else -> value
        }
    }
}