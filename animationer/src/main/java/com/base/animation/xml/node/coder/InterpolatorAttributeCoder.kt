package com.base.animation.xml.node.coder

import kotlin.reflect.KClass

class InterpolatorAttributeCoder : IAttributeCoder<Int> {
    override fun attributeDecode(needType: KClass<*>, value: String): Any? {
        return if (needType == Int::class) {
            value.toInt()
        } else {
            null
        }
    }

    override fun attributeEncode(value: Any?): Int? {
        return if (value is Int) {
            value
        } else {
            null
        }
    }
}

enum class InterpolatorEnum(val type: Int) {
    Linear(0), Accelerate(1), Decelerate(2)
}