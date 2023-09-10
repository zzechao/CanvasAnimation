package com.base.animation.xml.node.coder

import com.base.animation.model.PathObject
import com.base.animation.xml.XmlWriterHelper

interface IXmlObjNodeParser {
    fun encode(write: XmlWriterHelper)

    fun decode(id: String): PathObject?
}