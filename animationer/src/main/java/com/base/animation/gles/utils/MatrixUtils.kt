package com.base.animation.gles.utils

import android.graphics.Bitmap
import android.opengl.Matrix
import android.util.Log

/**
 * @author zzechao
 * @date 2025/3/20 10:41
 */
object MatrixUtils {
    const val TYPE_FITXY: Int = 0
    const val TYPE_CENTERCROP: Int = 1
    const val TYPE_CENTERINSIDE: Int = 2
    const val TYPE_FITSTART: Int = 3
    const val TYPE_FITEND: Int = 4

    fun getMatrix(
        matrix: FloatArray?, type: Int, imgWidth: Int, imgHeight: Int, viewWidth: Int, viewHeight: Int
    ) {
        if (imgHeight > 0 && imgWidth > 0 && viewWidth > 0 && viewHeight > 0) {
            val projection = FloatArray(16)
            val camera = FloatArray(16)
            if (type == TYPE_FITXY) {
                Matrix.orthoM(projection, 0, -1f, 1f, -1f, 1f, 1f, 3f)
                Matrix.setLookAtM(camera, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f)
                Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0)
            }
            val sWhView = viewWidth.toFloat() / viewHeight
            val sWhImg = imgWidth.toFloat() / imgHeight
            if (sWhImg > sWhView) {
                when (type) {
                    TYPE_CENTERCROP -> Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1f, 1f, 1f, 3f)
                    TYPE_CENTERINSIDE -> Matrix.orthoM(projection, 0, -1f, 1f, -sWhImg / sWhView, sWhImg / sWhView, 1f, 3f)
                    TYPE_FITSTART -> Matrix.orthoM(projection, 0, -1f, 1f, 1 - 2 * sWhImg / sWhView, 1f, 1f, 3f)
                    TYPE_FITEND -> Matrix.orthoM(projection, 0, -1f, 1f, -1f, 2 * sWhImg / sWhView - 1, 1f, 3f)
                }
            } else {
                when (type) {
                    TYPE_CENTERCROP -> Matrix.orthoM(projection, 0, -1f, 1f, -sWhImg / sWhView, sWhImg / sWhView, 1f, 3f)
                    TYPE_CENTERINSIDE -> Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1f, 1f, 1f, 3f)
                    TYPE_FITSTART -> Matrix.orthoM(projection, 0, -1f, 2 * sWhView / sWhImg - 1, -1f, 1f, 1f, 3f)
                    TYPE_FITEND -> Matrix.orthoM(projection, 0, 1 - 2 * sWhView / sWhImg, 1f, -1f, 1f, 1f, 3f)
                }
            }
            Matrix.setLookAtM(camera, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f)
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0)
        }
    }

    fun getCenterInsideMatrix(matrix: FloatArray?, imgWidth: Int, imgHeight: Int, viewWidth: Int, viewHeight: Int) {
        if (imgHeight > 0 && imgWidth > 0 && viewWidth > 0 && viewHeight > 0) {
            val sWhView = viewWidth.toFloat() / viewHeight
            val sWhImg = imgWidth.toFloat() / imgHeight
            val projection = FloatArray(16)
            val camera = FloatArray(16)
            if (sWhImg > sWhView) {
                Matrix.orthoM(projection, 0, -1f, 1f, -sWhImg / sWhView, sWhImg / sWhView, 1f, 3f)
            } else {
                Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1f, 1f, 1f, 3f)
            }
            Matrix.setLookAtM(camera, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f)
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0)
        }
    }

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

    val originalMatrix: FloatArray
        get() {
            val identityMatrix = FloatArray(16)
            Matrix.setIdentityM(identityMatrix, 0)
            return identityMatrix
        }

    fun calculateMatrixForBitmap(bitmap: Bitmap, width: Int, height: Int): FloatArray {
        val mBitmap = bitmap
        val w = mBitmap.width
        val h = mBitmap.height
        val sWH = w / h.toFloat()
        val sWidthHeight = width / height.toFloat()
        //        uXY=sWidthHeight;
        val mViewMatrix = FloatArray(16)
        val mProjectMatrix = FloatArray(16)
        val mModelMatrix = FloatArray(16)
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1f, 1f, 3f, 5f)
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1f, 1f, 3f, 5f)
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -1f, 1f, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3f, 5f)
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -1f, 1f, -sWH / sWidthHeight, sWH / sWidthHeight, 3f, 5f)
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        //计算变换矩阵
        Matrix.multiplyMM(mModelMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
        return mModelMatrix
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