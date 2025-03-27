package com.base.animation.gles

import android.opengl.GLES20
import com.base.animation.Animer
import com.base.animation.gles.utils.GLESUtils

/**
 * @author zzechao
 * @date 2025/3/20 18:39
 * 着色器
 */
class EGLAnimShader {
    companion object {
        private const val TAG = "EGLAnimShader"
    }

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
    private val fragmentShaderCode = """#extension GL_OES_EGL_image_external : require
            precision mediump float;
            uniform sampler2D vTexture;
            uniform samplerExternalOES vTextureOES;
            uniform float uAlpha;
            uniform bool uIsColor2D;
            varying vec2 aTexCoordinate;
            
            void main() {
                vec4 color2D = texture2D(vTexture, aTexCoordinate);
                vec4 colorOES = texture2D(vTextureOES, aTexCoordinate);
            
                if (uIsColor2D) {
                    gl_FragColor = vec4(color2D.rgb, color2D.a * uAlpha);
                } else {
                    gl_FragColor = vec4(colorOES.rgb, colorOES.a * uAlpha);
                }
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

    var uAlphaHandle = 0

    var vTextureOESHandle = 0

    var uIsColor2DHandle = 0

    fun initShader() {
        val vertexShader = GLESUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = GLESUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)
        GLES20.glLinkProgram(mProgram)

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        texCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "vTexCoordinate")
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        texHandle = GLES20.glGetUniformLocation(mProgram, "vTexture")
        vTextureOESHandle = GLES20.glGetUniformLocation(mProgram, "vTextureOES")
        uAlphaHandle = GLES20.glGetUniformLocation(mProgram, "uAlpha")
        uIsColor2DHandle = GLES20.glGetUniformLocation(mProgram, "uIsColor2D")

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
        Animer.log.i(TAG, "destroyShader")
        unUseShader()

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordinateHandle)
        GLES20.glDisableVertexAttribArray(texHandle)
        GLES20.glDisableVertexAttribArray(vPMatrixHandle)
        GLES20.glDisableVertexAttribArray(uAlphaHandle)
        GLES20.glDisableVertexAttribArray(vTextureOESHandle)
        GLES20.glDisableVertexAttribArray(uIsColor2DHandle)

        GLES20.glDetachShader(mProgram, GLES20.GL_VERTEX_SHADER)
        GLES20.glDeleteShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glDetachShader(mProgram, GLES20.GL_FRAGMENT_SHADER)
        GLES20.glDeleteShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glDeleteProgram(mProgram)
        GLES20.glReleaseShaderCompiler()
        Animer.log.i(TAG, "destroyShader end")
    }
}