package com.base.animation.xml.node

import com.base.animation.IAnimView
import com.base.animation.model.AnimPathObject
import com.base.animation.model.PathObject

class AnimNodeChain(val anim: IAnimView) {
    var curDisplayId = ""
    var path: AnimPathObject.Inner? = null
    var isBeginPointSet = false
    var nextStart: PathObject? = null
}