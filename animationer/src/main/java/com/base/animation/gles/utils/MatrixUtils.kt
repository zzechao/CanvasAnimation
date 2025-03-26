package com.base.animation.gles.utils

import android.opengl.Matrix
import android.util.Log

/**
 * @author zzechao
 * @date 2025/3/20 10:41
 */
object MatrixUtils {
    fun rotate(m: FloatArray, angle: Float): FloatArray {
        Matrix.rotateM(m, 0, angle, 0f, 0f, 1f)
        return m
    }

    fun flip(m: FloatArray, x: Boolean, y: Boolean): FloatArray {
        if (x || y) {
            Matrix.scaleM(m, 0, (if (x) -1 else 1).toFloat(), (if (y) -1 else 1).toFloat(), 1f)
        }
        return m
    }

    fun scale(m: FloatArray, x: Float, y: Float): FloatArray {
        Matrix.scaleM(m, 0, x, y, 1f)
        return m
    }

    fun translate(m: FloatArray, x: Float, y: Float): FloatArray {
        Matrix.translateM(m, 0, x, y, 1f)
        return m
    }
}

fun FloatArray.rotate(angle: Float): FloatArray {
    Log.i("MatrixUtils", "rotate: $angle")
    return MatrixUtils.rotate(this, angle)
}


fun FloatArray.flip(x: Boolean, y: Boolean): FloatArray {
    return MatrixUtils.flip(this, x, y)
}

fun FloatArray.scale(x: Float, y: Float): FloatArray {
    return MatrixUtils.scale(this, x, y)
}

fun FloatArray.translate(x: Float, y: Float): FloatArray {
    return MatrixUtils.translate(this, x, y)
}