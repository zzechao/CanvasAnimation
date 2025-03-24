package com.base.animation.gles

import android.graphics.SurfaceTexture
import android.os.Build
import com.base.animation.Animer
import com.base.animation.CanvasHandler
import com.base.animation.common.AnimPlayer
import com.base.animation.fpsTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor

/**
 * @author zzechao
 * @date 2025/3/19 15:46
 */
class EGLAnimPlayer(private val render: EGLRender = EGLRender()) : AnimPlayer(false),
    IRenderer by render, CanvasHandler.CanvasFrameCallback {

    companion object {
        private const val TAG = "EGLAnimPlayer"
    }

    override var mSurface: SurfaceTexture? = null

    private val glScope by lazy { CoroutineScope(Animer.glThreadDispatcher) }
    private val glActor = glScope.actor<EGLAction>(Animer.exceptionHandler, capacity = 20) {
        for (msg in channel) {
            Animer.log.d(TAG, "actor EGLAction:${msg.description()} run")
            msg.action()
        }
    }

    override fun resume() {
        glActor.offer(EGLAction(EGLAction.MSG_RESUME) {
            super.resume()
        })
    }

    override fun pause() {
        glActor.offer(EGLAction(EGLAction.MSG_PAUSE) {
            super.pause()
        })
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        glActor.offer(EGLAction(EGLAction.MSG_INIT) {
            mSurface = surface
            render.onSurfaceTextureAvailable(surface, width, height)
            setCanvasFrameCallback(this)
        })
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        glActor.offer(EGLAction(EGLAction.MSG_SIZE_CHANGED) {
            render.onSurfaceTextureSizeChanged(surface, width, height)
        })
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        glActor.offer(EGLAction(EGLAction.MSG_RELEASE) {
            setCanvasFrameCallback(null)
            render.onSurfaceTextureDestroyed(surface)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !surface.isReleased) {
                surface.release()
            } else {
                kotlin.runCatching { surface.release() }
            }
            endAnimation()
        })
        return false
    }

    override fun doCanvasFrame(frameTime: Long): Boolean {
        val framePositionCount = if (frameTime == 0L) {
            1
        } else {
            val framePositionCount = frameTime / fpsTime
            if (framePositionCount <= 1) {
                1
            } else {
                framePositionCount.toInt()
            }
        }
        glActor.offer(EGLAction(EGLAction.MSG_PLAY) {
            val ids = pathObjectDeal.animDrawIds.toList()
            val data = pathObjectDeal.animDrawObjects.toMap()
            ids.forEach { data[it]?.drawRender(render, pathObjectDeal, framePositionCount, frameTime) }
        })
        return true
    }
}