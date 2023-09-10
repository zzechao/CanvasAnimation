package com.base.animation.xml.node

import com.base.animation.xml.node.coder.DefaultAttributeCoder
import com.base.animation.xml.node.coder.IAttributeCoder
import java.lang.annotation.Documented
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@Documented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class AnimAttributeName(
    val name: String,
    val coder: KClass<out IAttributeCoder<out Any>> = DefaultAttributeCoder::class
)
