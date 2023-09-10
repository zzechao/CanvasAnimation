package com.base.animation.xml.node.coder

import com.google.gson.Gson
import java.lang.reflect.Type

object ValueLoader {
    private val gson by lazy { Gson() }

    fun <T> fromObject(json: String, typeOfT: Type): T {
        return gson.fromJson(json, typeOfT)
    }

    fun toJsonString(any: Any): String {
        return gson.toJson(any)
    }
}