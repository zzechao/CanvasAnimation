package com.base.animation.xml.node.coder

import kotlin.reflect.KClass

interface IAttributeCoder<T> {
    /**
     * 解码接口
     * @param needType 希望本函数返回的类型
     * @param value 将要被解码的值
     * @return 解码后返回的值
     */
    fun attributeDecode(needType: KClass<*>, value: String): Any?

    /**
     * 编码接口
     * @param value 将要被编码的值
     * @return 编码后返回的值
     */
    fun attributeEncode(value: Any?): T?
}