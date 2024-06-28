package com.base.animation.helper.data

import android.view.animation.BaseInterpolator
import androidx.annotation.Keep
import com.base.animation.model.AnimDrawObject

/**
 * @author:zhouzechao
 * description：*
 */
@Keep
data class PathProcess(
    val start: AnimDrawObject,
    val interpolator: BaseInterpolator,
    val durTime: Long, var curTotalTime: Float = 0f,
    val item: PathProcessItem,
    val current: AnimDrawObject,
    val clickable: Boolean,
    val extra: String = ""
)


data class PathProcessItem(
    val totalX: Float,
    val totalY: Float,
    val totalAlpha: Int,
    val totalScaleX: Float,
    val totalScaleY: Float,
    val totalRotation: Float
)