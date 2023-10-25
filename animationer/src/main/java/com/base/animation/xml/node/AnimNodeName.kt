package com.base.animation.xml.node

import java.lang.annotation.Documented
import java.lang.annotation.Inherited

/**
 * @author:zhouzechao
 * description：*
 */
@Documented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class AnimNodeName(val name: String)
