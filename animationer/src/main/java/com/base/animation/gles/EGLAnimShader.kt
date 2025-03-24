package com.base.animation.gles

/**
 * @author zzechao
 * @date 2025/3/20 18:39
 */
class EGLAnimShader {
    /**
     * 绘制的流程
     * 1.顶点着色程序 - 用于渲染形状的顶点的 OpenGL ES 图形代码
     * 2.片段着色器 - 用于渲染具有特定颜色或形状的形状的 OpenGL ES 代码纹理。
     * 3.程序 - 包含您想要用于绘制的着色器的 OpenGL ES 对象 一个或多个形状
     *
     *
     * 您至少需要一个顶点着色器来绘制形状，以及一个 fragment 着色器来为该形状着色。
     * 这些着色器必须经过编译，然后添加到 OpenGL ES 程序中，该程序随后用于绘制形状。
     */
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
}