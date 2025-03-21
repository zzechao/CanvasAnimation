package com.base.animation.item

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Build
import android.text.*
import androidx.core.text.TextDirectionHeuristicsCompat
import com.base.animation.DoubleLinkedReference
import com.base.animation.OnAnimItemClick
import com.base.animation.model.AnimDrawObject

/**
 * @author:zhouzechao
 * @date: 2020/12/23
 * description：单个view的绘制item
 */
class StringDisplayItem(
    fontSize: Int, message: String, txtColor: Int, private val maxWidth: Int, private val singleLine: Boolean
) : BaseDisplayItem() {

    private val paint by lazy {
        TextPaint().apply {
            color = txtColor
            style = Paint.Style.FILL
            textSize = fontSize.toFloat()
        }
    }

    private var txtStaticLayout: StaticLayout? = null

    init {
        displayHeight = fontSize
        displayWidth = displayHeight * message.length
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtStaticLayout = StaticLayout.Builder.obtain(
                message, 0, message.length, paint, if (maxWidth > 0) maxWidth else displayWidth
            ).apply {
                if (singleLine) {
                    setMaxLines(1)
                    setEllipsize(TextUtils.TruncateAt.END)
                    setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE)
                }
            }.build()
        } else if (singleLine) {
            StaticLayout(
                message, 0, message.length, paint, if (maxWidth > 0) maxWidth else displayWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, if (maxWidth > 0) maxWidth else displayWidth
            )
        } else {
            StaticLayout(
                message, 0, message.length, paint, if (maxWidth > 0) maxWidth else displayWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
            )
        }
    }

    override fun drawDisplayItem(canvas: Canvas, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float) {
        txtStaticLayout?.paint?.alpha = alpha
        canvas.translate(x, y)
        txtStaticLayout?.draw(canvas)
    }

    override fun getScalePX(scaleX: Float): Float {
        return if (maxWidth > 0) maxWidth / 2f else displayWidth / 2f
    }

    override fun getScalePY(scaleY: Float): Float {
        return if (maxWidth > 0 && !singleLine) (txtStaticLayout?.lineCount ?: 1) * displayHeight / 2f else displayHeight / 2f
    }

    override fun touch(animId: Long, onAnimItemClick: OnAnimItemClick, animDrawObject: AnimDrawObject, touchPoint: DoubleLinkedReference<PointF>, extra: String) {
    }
}