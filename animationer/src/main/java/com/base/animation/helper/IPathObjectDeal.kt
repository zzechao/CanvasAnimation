package com.base.animation.helper

import com.base.animation.AnimCache
import com.base.animation.item.BaseDisplayItem
import com.base.animation.model.AnimPathObject
import com.base.animation.model.BaseAnimDrawObject

interface IPathObjectDeal {

    val animDrawObjects: MutableMap<Long, BaseAnimDrawObject>

    fun getDisplayItem(displayItemId: String): BaseDisplayItem? {
        return AnimCache.displayItemCache.getDisplayItem(displayItemId)
    }

    fun sendAnimPath(animPathObject: AnimPathObject)

    fun hasTask(): Boolean

    fun removeAnimId(animId: Long)
}