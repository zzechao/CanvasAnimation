package com.base.animation.gles

import android.graphics.SurfaceTexture
import android.util.Log
import com.base.animation.Animer
import com.base.animation.CanvasHandler
import com.base.animation.DoubleLinkedReference
import com.base.animation.common.AnimPlayer
import com.base.animation.fpsTime
import com.base.animation.model.AnimPathObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import java.util.concurrent.Executors

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

    private var glScope: CoroutineScope? = null
    private var glActor: SendChannel<EGLAction>? = null

    override fun resume() {
        kotlin.runCatching {
            glActor?.offer(EGLAction(EGLAction.MSG_RESUME) {
                super.resume()
            })
        }.onFailure {
            release()
        }
    }

    override fun pause() {
        kotlin.runCatching {
            glActor?.offer(EGLAction(EGLAction.MSG_PAUSE) {
                super.pause()
            })
        }.onFailure {
            release()
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        kotlin.runCatching {
            glActor?.offer(EGLAction(EGLAction.MSG_INIT) {
                render.onSurfaceTextureAvailable(surface, width, height)
            })
        }.onFailure {
            release()
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        kotlin.runCatching {
            glActor?.offer(EGLAction(EGLAction.MSG_SIZE_CHANGED) {
                render.onSurfaceTextureSizeChanged(surface, width, height)
            })
        }.onFailure {
            release()
        }
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return render.onSurfaceTextureDestroyed(surface)
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
                if (ids.isNotEmpty()) {
                    render.drawRenderBegin()
                    ids.forEach { data[it]?.drawRender(render, pathObjectDeal, framePositionCount, frameTime) }
                    render.drawRenderEnd()
                    mTouchPointF?.let { DoubleLinkedReference(it) }?.let {
                        val size = ids.size - 1
                        for (index in size downTo 0) {
                            data[ids[index]]?.touch(pathObjectDeal, it)
                        }
                        mTouchPointF = null
                    }
                } else {
                    pause()
                    render.drawRenderBegin()
                    render.drawRenderEnd()
                    pathObjectDeal.animDrawObjects.clear()
                }
            })
        }.onFailure {
            release()
        }
        return true
    }

    override fun addAnimDisplay(animPathObject: AnimPathObject) {
        super.addAnimDisplay(animPathObject)
        setCanvasFrameCallback(this)
    }

    fun onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow")
        glActor?.offer(EGLAction(EGLAction.MSG_RELEASE) {
            release()
            glActor?.close()
            glScope?.cancel()
        })
    }

    private fun release() {
        setCanvasFrameCallback(null)
        endAnimation()
        render.release()
    }

    fun onAttachedToWindow() {
        initActor()
    }

    private fun initActor() {
        glActor?.close()
        glScope?.cancel()
        glScope = CoroutineScope(
            Executors.newSingleThreadExecutor().asCoroutineDispatcher() + Animer.exceptionHandler
        )
        glActor = glScope?.actor(capacity = 50) {
            for (msg in channel) {
                Animer.log.d(TAG, "actor EGLAction:${msg.description()} run")
                msg.action()
            }
        }
        glActor?.invokeOnClose {
            release()
        }
    }
}