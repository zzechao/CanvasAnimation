package com.base.animation

import com.base.animation.item.BaseDisplayItem
import java.security.MessageDigest
import kotlin.reflect.KClass

/**
 * @author:zhouzechao
 * @date: 3/31/21
 * description：处理是否要重新获取bitmap
 */
class DisplayObject private constructor() {

    private val displayItems = mutableListOf<BaseDisplayItem>()

    companion object {
        @JvmStatic
        fun with(
        ): DisplayObject {
            return DisplayObject()
        }
    }

    fun <T : BaseDisplayItem> add(
        key: String, displayWidth: Int, displayHeight: Int, kClass:
        KClass<out BaseDisplayItem>, create: () -> T?
    ): String {
        val md5Key = getDisPlayItemKey(key, displayWidth, displayHeight, kClass)
        if (!AnimCache.displayItemCache.hasDisplayItem(md5Key)) {
            val displayItem = create()?.apply {
                displayItemId = md5Key
            } ?: return ""
            displayItems.add(displayItem)
        }
        return md5Key
    }

    suspend fun <T : BaseDisplayItem> suspendAdd(
        key: String, displayWidth: Int, displayHeight: Int, kClass:
        KClass<out BaseDisplayItem>, create: suspend () -> T?
    ): String {
        val md5Key = getDisPlayItemKey(key, displayWidth, displayHeight, kClass)
        if (!AnimCache.displayItemCache.hasDisplayItem(md5Key)) {
            val displayItem = create()?.apply {
                displayItemId = md5Key
            } ?: return ""
            displayItems.add(displayItem)
        }
        return md5Key
    }

    fun <T : BaseDisplayItem> add(
        key: String,
        kClass: KClass<out BaseDisplayItem>,
        create: () -> T?
    ): String {
        val md5Key = getDisPlayItemKey(key, kClass = kClass)
        if (!AnimCache.displayItemCache.hasDisplayItem(md5Key)) {
             val displayItem = create()?.apply {
                displayItemId = md5Key
            } ?: return ""
            displayItems.add(displayItem)
        }
        return md5Key
    }

    suspend fun <T : BaseDisplayItem> suspendAdd(
        key: String,
        kClass: KClass<out BaseDisplayItem>,
        create: suspend () -> T?
    ): String {
        val md5Key = getDisPlayItemKey(key, kClass = kClass)
        if (!AnimCache.displayItemCache.hasDisplayItem(md5Key)) {
            val displayItem = create()?.apply {
                displayItemId = md5Key
            } ?: return ""
            displayItems.add(displayItem)
        }
        return md5Key
    }

    fun build(): List<BaseDisplayItem> {
        return displayItems
    }

    private fun md5(str: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val result = digest.digest(str.toByteArray())
        //转成16进制后是32字节
        return toHex(result)
    }

    private fun toHex(byteArray: ByteArray): String {
        //转成16进制后是32字节
        return with(StringBuilder()) {
            byteArray.forEach {
                val hex = it.toInt() and (0xFF)
                val hexStr = Integer.toHexString(hex)
                if (hexStr.length == 1) {
                    append("0").append(hexStr)
                } else {
                    append(hexStr)
                }
            }
            toString()
        }
    }

    /**
     * 获取display的md5信息
     */
    fun getDisPlayItemKey(
        key: String, displayWidth: Int = 0, displayHeight: Int = 0, kClass: KClass<out
        BaseDisplayItem>
    ): String {
        return md5("$key$displayWidth$displayHeight$kClass")
    }
}