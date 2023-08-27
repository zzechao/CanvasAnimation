package com.base.animation.xml

import android.util.Xml
import java.io.StringWriter
import java.io.Writer

/**
 * 构建xml的string
 */
class XmlWriterHelper {

    private val xmlSerializer by lazy { Xml.newSerializer() }

    companion object {
        fun newString(onInit: (XmlWriterHelper.() -> Unit)?): String {
            val stringWriter = StringWriter()
            XmlWriterHelper().apply {
                xmlSerializer.setOutput(stringWriter as Writer?)
                xmlSerializer.startDocument("utf-8", true)
                onInit?.invoke(this)
                xmlSerializer.endDocument()
                xmlSerializer.flush()
            }
            return stringWriter.toString()
        }
    }

    fun node(name: String, onNodeSet: (XmlWriterHelper.() -> Unit)?) {
        xmlSerializer.startTag(null, name)
        onNodeSet?.invoke(this)
        xmlSerializer.endTag(null, name)
    }

    fun atttibute(attriName: String, attriValue: String) {
        xmlSerializer.attribute(null, attriName, attriValue)
    }

    fun text(text: String) {
        xmlSerializer.text(text)
    }
}