package com.base.animation


/**
 * @author:zhouz
 * @date: 2024/7/1 16:33
 * description：引用
 */
class DoubleLinkedReference<T>(var data: T? = null) {
    fun reset() {
        data = null
    }
}