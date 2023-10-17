package com.base.animation.node

import android.graphics.PointF
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.base.animation.IAnimView
import com.base.animation.model.PathObject
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.AnimNodeName
import com.base.animation.xml.node.coder.DefaultAttributeCoder
import com.base.animation.xml.node.coder.InterpolatorAttributeCoder
import com.base.animation.xml.node.coder.InterpolatorEnum
import com.base.animation.xml.node.coder.LayoutIDAttributeCoder
import com.base.animation.xml.node.coder.LocationAttributeCoder
import com.base.animation.xml.node.coder.UrlAttributeCoder

@AnimNodeName("endAnim")
class EndNode : IAnimNode {

    @AnimAttributeName("endL", LocationAttributeCoder::class)
    @JvmField
    var point: PointF? = null

    @AnimAttributeName("endIdName", LayoutIDAttributeCoder::class)
    @JvmField
    var layoutIdName: String = ""

    @AnimAttributeName("url", UrlAttributeCoder::class)
    @JvmField
    var url: String = ""

    @AnimAttributeName("scaleX", DefaultAttributeCoder::class)
    @JvmField
    var scaleX = 1f

    @AnimAttributeName("scaleY", DefaultAttributeCoder::class)
    @JvmField
    var scaleY = 1f

    @AnimAttributeName("durTime", DefaultAttributeCoder::class)
    @JvmField
    var durTime: Long = 0

    @AnimAttributeName("interpolator", InterpolatorAttributeCoder::class)
    @JvmField
    var interpolator = InterpolatorEnum.Linear.type

    @AnimAttributeName("alpha")
    @JvmField
    var alpha = 255

    @AnimAttributeName("rotation", DefaultAttributeCoder::class)
    @JvmField
    var rotation = 0f

    override fun decode(id: String, anim: IAnimView): PathObject {
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
        val pointF =
            if ((point?.x ?: 0f) == 0f && (point?.y ?: 0f) == 0f && layoutIdName.isNotEmpty()) {
                anim.getViewByAnimName(layoutIdName)?.let { it ->
                    getCenterOfViewLocationInWindow(it).let {
                        PointF(it[0].toFloat(), it[1].toFloat())
                    }
                } ?: PointF()
            } else {
                point ?: PointF()
            }
        return PathObject(
            id, pointF, alpha, scaleX, scaleY, rotation, interpolator = interpolatorObject
        )
    }
}