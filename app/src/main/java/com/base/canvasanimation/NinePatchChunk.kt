package com.base.canvasanimation

import android.graphics.Rect
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * 创建点9图
 */
class NinePatchChunk {

    var wasSerialized: Byte = 1
    var numXDivs: Byte = 2
    var numYDivs: Byte = 2
    var numColors: Byte = 0
    var mPaddings = Rect()
    var mDivX: IntArray = intArrayOf()
    var mDivY: IntArray = intArrayOf()
    var mColor: IntArray = intArrayOf()
    fun praseNinePatchChunk(data: ByteArray?) {
        val byteBuffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        wasSerialized = byteBuffer.get()
        //        if (wasSerialized == 0) return null;
        numXDivs = byteBuffer.get()
        numYDivs = byteBuffer.get()
        numColors = byteBuffer.get()
        mDivX = IntArray(numXDivs.toInt())
        mDivY = IntArray(numYDivs.toInt())
        mColor = IntArray(numColors.toInt())
        checkDivCount(mDivX.size)
        checkDivCount(mDivY.size)

        // skip 8 bytes
        byteBuffer.int
        byteBuffer.int
        mPaddings.left = byteBuffer.int
        mPaddings.right = byteBuffer.int
        mPaddings.top = byteBuffer.int
        mPaddings.bottom = byteBuffer.int

        // skip 4 bytes
        byteBuffer.int
        readIntArray(mDivX, byteBuffer)
        readIntArray(mDivY, byteBuffer)
        readIntArray(mColor, byteBuffer)
    }

    fun toData(): ByteArray? {
        val dataSize = 32 + (mDivX.size + mDivY.size + mColor.size) * 4
        val bb = ByteBuffer.allocate(dataSize).order(ByteOrder.LITTLE_ENDIAN)
        bb.put(wasSerialized)
        bb.put(numXDivs)
        bb.put(numYDivs)
        bb.put(numColors)
        bb.putInt(0)
        bb.putInt(0)
        bb.putInt(mPaddings.left)
        bb.putInt(mPaddings.top)
        bb.putInt(mPaddings.right)
        bb.putInt(mPaddings.bottom)
        bb.putInt(0)
        writeIntArray(mDivX, bb)
        writeIntArray(mDivY, bb)
        writeIntArray(mColor, bb)
        return bb.array()
    }

    companion object {
        const val NO_COLOR = 0x1
        const val TRANSPARENT_COLOR = 0x0
        fun createInstance(data: ByteArray?): NinePatchChunk {
            val npc = NinePatchChunk()
            npc.praseNinePatchChunk(data)
            return npc
        }

        //创建拉伸块
        //[left，right]上边缘黑色区域，px为单位
        //[top，bottom]左边缘黑色区域
        fun createInstance(left: Int, right: Int, top: Int, bottom: Int): NinePatchChunk? {
            if (left > right || top > bottom) {
                return null
            }
            val npc = NinePatchChunk()
            npc.numXDivs = 2
            npc.mDivX = IntArray(npc.numXDivs.toInt())
            npc.mDivX[0] = left
            npc.mDivX[1] = right
            npc.numYDivs = 2
            npc.mDivY = IntArray(npc.numYDivs.toInt())
            npc.mDivY[0] = top
            npc.mDivY[1] = bottom
            npc.numColors = 9
            npc.mColor = IntArray(npc.numColors.toInt())
            var i = 0
            val n = npc.mColor.size
            while (i < n) {
                npc.mColor[i] = NO_COLOR
                ++i
            }
            npc.mPaddings.left = 0
            npc.mPaddings.top = 0
            npc.mPaddings.right = 0
            npc.mPaddings.bottom = 0
            return npc
        }

        fun createInstance(left: IntArray?, top: IntArray?): NinePatchChunk? {
            if (left == null || top == null) {
                return null
            }
            if (left.size % 2 != 0 || top.size % 2 != 0) {
                return null
            }
            val npc = NinePatchChunk()
            npc.numXDivs = top.size.toByte()
            npc.mDivX = IntArray(npc.numXDivs.toInt())
            for (i in 0 until npc.numXDivs) {
                npc.mDivX[i] = top[i]
            }
            npc.numYDivs = left.size.toByte()
            npc.mDivY = IntArray(npc.numYDivs.toInt())
            for (i in 0 until npc.numYDivs) {
                npc.mDivY[i] = left[i]
            }

//    npc.numColors = 3;
//    npc.mColor = new int[npc.numColors];
//    npc.mColor[0] = NO_COLOR;
//    npc.mColor[1] = -45208;
//    npc.mColor[2] = NO_COLOR;
            npc.numColors = 9
            npc.mColor = IntArray(npc.numColors.toInt())
            var i = 0
            val n = npc.mColor.size
            while (i < n) {
                npc.mColor[i] = NO_COLOR
                ++i
            }
            npc.mPaddings.left = 0
            npc.mPaddings.top = 0
            npc.mPaddings.right = 0
            npc.mPaddings.bottom = 0
            return npc
        }

        private fun readIntArray(data: IntArray, buffer: ByteBuffer) {
            var i = 0
            val n = data.size
            while (i < n) {
                data[i] = buffer.int
                ++i
            }
        }

        private fun checkDivCount(length: Int) {
            if (length == 0 || length and 0x01 != 0) {
                throw RuntimeException("invalid nine-patch: $length")
            }
        }

        private fun writeIntArray(data: IntArray?, buffer: ByteBuffer) {
            if (data != null) {
                var i = 0
                val n = data.size
                while (i < n) {
                    buffer.putInt(data[i])
                    ++i
                }
            }
        }
    }
}