package com.base.animation.node

import android.graphics.PointF
import android.util.Log
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.CallSuper
import com.base.animation.model.PathObject
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.AnimNodeName
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import com.base.animation.xml.node.coder.IAttributeCoder
import com.base.animation.xml.node.coder.InterpolatorAttributeCoder
import com.base.animation.xml.node.coder.InterpolatorEnum
import com.base.animation.xml.node.coder.LayoutIDAttributeCoder
import com.base.animation.xml.node.coder.LocationAttributeCoder
import com.base.animation.xml.node.coder.UrlAttributeCoder
import java.lang.reflect.Field

@AnimNodeName("endAnim")
class EndNode : IAnimNode {

    @AnimAttributeName("endL", LocationAttributeCoder::class)
    @JvmField
    var point: PointF? = null

    @AnimAttributeName("endId", LayoutIDAttributeCoder::class)
    @JvmField
    var layoutId: Long = 0

    @AnimAttributeName("url", UrlAttributeCoder::class)
    @JvmField
    var url: String = ""

    @AnimAttributeName("displaySize", DefaultAttributeCoder::class)
    @JvmField
    var displayHeightSize = 0

    @AnimAttributeName("scaleX", DefaultAttributeCoder::class)
    @JvmField
    var scaleX = 1f

    @AnimAttributeName("scaleY", DefaultAttributeCoder::class)
    @JvmField
    var scaleY = 1f

    @AnimAttributeName("durTime", DefaultAttributeCoder::class)
    @JvmField
    var durTime: Long = 1000

    @AnimAttributeName("interpolator", InterpolatorAttributeCoder::class)
    @JvmField
    var interpolator = InterpolatorEnum.Linear.type

    @AnimAttributeName("alpha")
    @JvmField
    var alpha = 255

    @AnimAttributeName("rotation", DefaultAttributeCoder::class)
    @JvmField
    var rotation = 0f

    override fun decode(id: String): PathObject {
        val interpolatorObject = when (interpolator) {
            InterpolatorEnum.Linear.type -> {
                LinearInterpolator()
            }

            InterpolatorEnum.Accelerate.type -> {
                AccelerateInterpolator()
            }

            InterpolatorEnum.Decelerate.type -> {
                DecelerateInterpolator()
            }

            else -> {
                LinearInterpolator()
            }
        }
        return PathObject(
            id,
            point ?: PointF(),
            alpha,
            scaleX,
            scaleY,
            rotation,
            interpolator = interpolatorObject
        )
    }
}