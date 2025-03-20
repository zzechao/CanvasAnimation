package com.base.animation.gles

import com.base.animation.common.AnimPlayer

/**
 * @author zzechao
 * @date 2025/3/19 15:46
 */
class EGLAnimPlayer : AnimPlayer(false), IRenderer by EGLRender() {
}