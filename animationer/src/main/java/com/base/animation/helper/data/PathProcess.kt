package com.base.animation.helper.data

import android.view.animation.BaseInterpolator
import androidx.annotation.Keep
import com.base.animation.model.AnimDrawObject

@Keep
data class PathProcess(
    val start: AnimDrawObject, var end: AnimDrawObject,
    var cur: AnimDrawObject, val interpolator: BaseInterpolator,
    val durTime: Long, var curTotalTime: Float = 0f,
    val item: PathProcessItem
)


data class PathProcessItem(
    val totalX: Float,
    val totalY: Float,
    val totalAlpha: Int,
    val totalScaleX: Float,
    val totalScaleY: Float,
    val totalRotation: Float
)