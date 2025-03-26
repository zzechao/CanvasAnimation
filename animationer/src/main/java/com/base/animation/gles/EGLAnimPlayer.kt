package com.base.animation.gles

import android.graphics.SurfaceTexture
import android.os.Build
import com.base.animation.Animer
import com.base.animation.Animer.calculationThreadFactory
import com.base.animation.CanvasHandler
import com.base.animation.common.AnimPlayer
import com.base.animation.fpsTime
import com.base.animation.model.AnimPathObject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author zzechao
 * @date 2025/3/19 15:46
 */
class EGLAnimPlayer(private val render: EGLRender = EGLRender()) : AnimPlayer(false),
    IRenderer by render,
    CanvasHandler.CanvasFrameCallback {

    companion object {
        private const val TAG = "EGLAnimPlayer"
    }

    private var isReleased: Boolean = false
    override var mSurface: SurfaceTexture? = null


    private var glScope: CoroutineScope? = null
    private var glActor: SendChannel<EGLAction>? = null

    override fun resume() {
        kotlin.runCatching {
            glActor?.offer(EGLAction(EGLAction.MSG_RESUME) {
                super.resume()
            })
        }
    }

    override fun pause() {
        kotlin.runCatching {
            glActor?.offer(EGLAction(EGLAction.MSG_PAUSE) {
                super.pause()
            })
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        kotlin.runCatching {
            glActor?.offer(EGLAction(EGLAction.MSG_INIT) {
                mSurface = surface
                render.onSurfaceTextureAvailable(surface, width, height)
            })
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        kotlin.runCatching {
            glActor?.offer(EGLAction(EGLAction.MSG_SIZE_CHANGED) {
                render.onSurfaceTextureSizeChanged(surface, width, height)
            })
        }
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        render.onSurfaceTextureDestroyed(surface)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !surface.isReleased) {
            surface.release()
        } else {
            kotlin.runCatching { surface.release() }
        }
        endAnimation()
        return false
    }

    override fun doCanvasFrame(frameTime: Long): Boolean {
        Animer.log.d(TAG, "doCanvasFrame")
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
        kotlin.runCatching {
            glActor?.offer(EGLAction(EGLAction.MSG_PLAY) {
                val ids = pathObjectDeal.animDrawIds.toList()
                val data = pathObjectDeal.animDrawObjects.toMap()
                render.drawRenderBegin()
                ids.forEach { data[it]?.drawRender(render, pathObjectDeal, framePositionCount, frameTime) }
                render.drawRenderEnd()
            })
        }
        return true
    }

    override fun addAnimDisplay(animPathObject: AnimPathObject) {
        super.addAnimDisplay(animPathObject)
        setCanvasFrameCallback(this)
    }

    fun onDetachedFromWindow() {
        isReleased = true
        setCanvasFrameCallback(null)
        endAnimation()
        render.release()
        glActor?.close()
        glScope?.cancel()
    }

    fun onAttachedToWindow() {
        isReleased = false
        initActor()
    }

    private fun initActor() {
        glScope = CoroutineScope(
            ThreadPoolExecutor(
                1, 1, 1000L, TimeUnit.MILLISECONDS, LinkedBlockingDeque(), calculationThreadFactory
            ).asCoroutineDispatcher() + SupervisorJob() + Animer.exceptionHandler
        )
        glActor = glScope?.actor(capacity = 50) {
            for (msg in channel) {
                if (isReleased) return@actor
                Animer.log.d(TAG, "actor EGLAction:${msg.description()} run")
                msg.action()
            }
        }
    }
}