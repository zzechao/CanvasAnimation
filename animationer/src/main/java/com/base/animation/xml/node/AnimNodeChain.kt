package com.base.animation.xml.node

import com.base.animation.IAnimView
import com.base.animation.model.AnimPathObject
import com.base.animation.model.PathObject
import com.base.animation.model.PathObjectWithDer

class AnimNodeChain(val anim: IAnimView) {
    var curDisplayId = ""
    var path: AnimPathObject.Inner? = null
    var isBeginPointSet = false
    var start: PathObject? = null
    var nextStart: PathObject? = null

    private val pathObjectStartEndContainer = mutableMapOf<ContainerEnd, PathObject>()

    fun buildAnim(nextPathObject: PathObject, delayed: Long) {
        if (isBeginPointSet) {
            nextStart?.let {
                it.displayItemId = nextPathObject.displayItemId
                it.interpolator = nextPathObject.interpolator
                path?.beginNextAnimPath(it)
            }
        } else {
            isBeginPointSet = true
            start?.let {
                it.interpolator = nextPathObject.interpolator
                path?.beginAnimPath(it)
            }
        }
        path?.doAnimPath(delayed, nextPathObject)
        nextStart = nextPathObject
    }

    fun recordAnimContainer(nextPathObject: PathObject, delayed: Long) {
        if (isBeginPointSet) {
            nextStart?.let {
                val start = it.copy()
                start.displayItemId = nextPathObject.displayItemId
                start.interpolator = nextPathObject.interpolator
                pathObjectStartEndContainer.put(ContainerEnd(nextPathObject, delayed), start)
            }
        } else {
            start?.let {
                val start = it.copy()
                start.displayItemId = nextPathObject.displayItemId
                start.interpolator = nextPathObject.interpolator
                pathObjectStartEndContainer.put(ContainerEnd(nextPathObject, delayed), start)
            }
        }
    }

    fun buildAnimContainer() {
        if (pathObjectStartEndContainer.isNotEmpty()) {
            if (isBeginPointSet) {
                path?.beginNextAnimPaths(pathObjectStartEndContainer.values.toMutableList())
            } else {
                path?.beginAnimPaths(pathObjectStartEndContainer.values.toMutableList())
                isBeginPointSet = true
            }

            val pathObjectsWithDer = pathObjectStartEndContainer.keys.map {
                PathObjectWithDer(it.delayed, it.pathObject)
            }
            path?.doAnimPaths(pathObjectsWithDer)
        }
    }

    data class ContainerEnd(val pathObject: PathObject, val delayed: Long)
}