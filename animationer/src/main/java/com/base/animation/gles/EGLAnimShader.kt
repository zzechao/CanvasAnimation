package com.base.animation.gles

/**
 * @author zzechao
 * @date 2025/3/20 18:39
 */
class EGLAnimShader {
    // 顶点着色器代码
    private val vertexShaderCode = """attribute vec4 vPosition;
        void main() {
          gl_Position = vPosition;
        }
        """

    // 片段着色器代码
    private val fragmentShaderCode = """precision mediump float;
        uniform vec4 vColor;
        void main() {
          gl_FragColor = vColor;
        }
        """
}