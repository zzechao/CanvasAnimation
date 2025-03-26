package com.base.animation.gles

import android.opengl.GLES20
import com.base.animation.gles.utils.GLESUtils.loadShader

/**
 * @author zzechao
 * @date 2025/3/20 18:39
 */
class EGLAnimShader {
    // 顶点着色器代码
    private val vertexShaderCode = """uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        attribute vec2 vTexCoordinate;
        varying vec2 aTexCoordinate;
        void main() {
          gl_Position = uMVPMatrix * vPosition;
          aTexCoordinate = vTexCoordinate;
        }
        """

    // 片段着色器代码
    private val fragmentShaderCode = """precision mediump float;
        uniform sampler2D vTexture;
        varying vec2 aTexCoordinate;
        void main() {
          gl_FragColor = texture2D(vTexture, aTexCoordinate);
        }
        """

    private var mProgram = 0

    var positionHandle = 0

    // 纹理坐标句柄
    var texCoordinateHandle = 0

    // 纹理Texture句柄
    var texHandle = 0

    // Use to access and set the view transformation
    var vPMatrixHandle = 0


    fun initShader() {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)
        GLES20.glLinkProgram(mProgram)

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        texCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "vTexCoordinate")
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        texHandle = GLES20.glGetUniformLocation(mProgram, "vTexture")

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(mProgram)
            mProgram = 0
        }
    }

    fun useShader() {
        GLES20.glUseProgram(mProgram)
    }

    fun unUseShader() {
        GLES20.glUseProgram(0)
    }

    fun destroyShader() {
        GLES20.glDeleteProgram(mProgram)
    }
}