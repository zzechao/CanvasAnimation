package com.base.animation.item

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.CallSuper
import com.base.animation.IAnimView
import com.base.animation.IDisplayItem
import com.base.animation.cache.IRecycle
import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass

/**
 * @author:zhouzechao
 * @date: 2020/12/8
 * description：display模块
 */
abstract class BaseDisplayItem constructor(paint: Paint) : IDisplayItem, IRecycle {

    open var displayHeight: Int = 0
    open var displayWidth: Int = 0
    private var itemId = animDisplayId.incrementAndGet()

    companion object {
        fun <T : BaseDisplayItem> of(
            iAnimView: IAnimView, clazz: KClass<out BaseDisplayItem>
        ): T? {
            return (iAnimView.obtain(clazz) as? T)
        }
    }

    override var displayItemId: String = itemId.toString()

    override var width: Int = 0

    override var height: Int = 0

    protected val mPaint: Paint = paint

    override fun attachView(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun draw(
        canvas: Canvas, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float,
        rotation: Float
    ) {
        canvas.save()
        canvas.scale(scaleX, scaleY, x + getScalePX(scaleX), y + getScalePY(scaleY))
        canvas.rotate(
            rotation, x + getRotatePX(rotation, scaleX),
            y + getRotatePY(rotation, scaleY)
        )

        draw(canvas, x, y, alpha, scaleX, scaleY)

        canvas.restore()
    }


    override fun poolSize(): Int {
        return 20
    }

    @CallSuper
    override fun reInit() {
        itemId = animDisplayId.incrementAndGet()
    }

    @CallSuper
    override fun recycle() {
        displayWidth = 0
        displayHeight = 0
        width = 0
        height = 0
    }

    override fun setDisplaySize(displayWidth: Int, displayHeight: Int) {
        this.displayWidth = displayWidth
        this.displayHeight = displayHeight
    }

    override fun getScalePX(scaleX: Float): Float {
        return 0f
    }

    override fun getScalePY(scaleY: Float): Float {
        return 0f
    }

    override fun getRotatePX(rotation: Float, scaleX: Float): Float {
        return 0f
    }

    override fun getRotatePY(rotation: Float, scaleY: Float): Float {
        return 0f
    }

    abstract fun draw(canvas: Canvas, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float)
}

val animDisplayId = AtomicLong(System.currentTimeMillis() / 1000L)