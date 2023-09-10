package com.base.animation.node

import android.util.Log
import com.base.animation.model.PathObject
import com.base.animation.xml.XmlWriterHelper
import com.base.animation.xml.node.AnimAttributeName
import com.base.animation.xml.node.AnimNodeName
import com.base.animation.xml.node.XmlBaseAnimNode
import com.base.animation.xml.node.coder.IXmlObjNodeParser
import java.lang.reflect.Field

private const val TAG = "IAnimNode"

interface IAnimNode : XmlBaseAnimNode, IXmlObjNodeParser {

    val nodeName: String
        get() {
            return javaClass.getAnnotation(AnimNodeName::class.java)?.name ?: ""
        }

    private fun parseAndGetField(write: XmlWriterHelper, field: Field) {
        try {
            val annotation = field.getAnnotation(AnimAttributeName::class.java) ?: return
            val fieldValue = field.get(this) ?: return
            val value = annotation.coder.java.newInstance().attributeEncode(fieldValue) ?: return
            write.atttibute(
                attriName = annotation.name,
                attriValue = value
            )
        } catch (e: Exception) {
            Log.e(TAG, "[parseAndGetField]:${e}")
        }
    }

    fun encodeSelf(write: XmlWriterHelper) {
    }

    override fun encode(write: XmlWriterHelper) {
        if (nodeName.isNotEmpty()) {
            write.node(nodeName) {
                try {
                    val javaClass = this@IAnimNode.javaClass
                    val fields = this@IAnimNode.javaClass.fields
                    Log.i(TAG, "[encode]:${this@IAnimNode.javaClass} ${fields.size} $javaClass")
                    for (i in fields.indices) {
                        parseAndGetField(this, fields[i])
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "[encode]:${e}")
                }
                encodeSelf(this)
            }
        }
    }

    override fun decode(id: String): PathObject? {
        return null
    }
}