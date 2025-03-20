package com.base.animation.gles

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
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)

        val shader = GLES20.glCreateShader(type)

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        return shader
    }
}
