package com.base.animation

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * @author:zhouzechao
 * description：*
 */
object BitmapLoader {
    fun decodeBitmapFrom(
        res: Resources,
        id: Int,
        sampleSize: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap =
        BitmapFactory.Options().run {
            // 如果期望的宽高是合法的, 则开启检测尺寸模式
            inPreferredConfig = Bitmap.Config.RGB_565

            var mSampleSize = sampleSize
            if (mSampleSize == 1) {
                inJustDecodeBounds = (reqWidth > 0 && reqHeight > 0)
                if (inJustDecodeBounds) {
                    BitmapFactory.decodeResource(res, id, this)
                    mSampleSize = calculate(this, reqWidth, reqHeight)
                }
            }

            inJustDecodeBounds = false
            // Calculate inSampleSize
            inSampleSize = mSampleSize
            inMutable = true
            return BitmapFactory.decodeResource(res, id, this)
        }

    private fun calculate(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        return calculate(height, width, reqWidth, reqHeight)
    }

    /**
     * https://developer.android.com/topic/performance/graphics/load-bitmap
     * 采用率计算方法
     */
    private fun calculate(height: Int, width: Int, reqWidth: Int, reqHeight: Int): Int {
        var inSampleSize = 1

        if (reqHeight <= 0 || reqWidth <= 0) {
            return inSampleSize
        }
        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}