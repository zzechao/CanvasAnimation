package com.base.animation.gles.utils

import android.opengl.GLES20

/**
 * @author zzechao
 * @date 2025/3/20 10:41
 */
object GLESUtils {
    /**
     * 加载着色器代码
     *
     * @param type
     * @param shaderCode
     * @return
     */
    fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}
