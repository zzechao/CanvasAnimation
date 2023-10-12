package com.base.animation.node

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import com.base.animation.AnimationEx
import com.base.animation.IAnimView
import com.base.animation.model.PathObject
import com.base.animation.xml.AnimDecoder2
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
            val animNodeName = javaClass.getAnnotation(AnimNodeName::class.java)?.name ?: return
            val key = "${animNodeName}_${annotation.name}"
            AnimDecoder2.mapNodeAttributeCoderMap[key]?.let {
                val value = it.attributeEncode(fieldValue) ?: return
                write.atttibute(annotation.name, value)
            } ?: kotlin.run {
                val attributeCoder = annotation.coder.java.newInstance()
                AnimDecoder2.mapNodeAttributeCoderMap[key] = attributeCoder
                val value = attributeCoder.attributeEncode(fieldValue) ?: return
                write.atttibute(
                    attriName = annotation.name,
                    attriValue = value
                )
            }
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

    override fun decode(id: String, anim: IAnimView): PathObject? {
        return null
    }

    @CallSuper
    override fun setAttribute(
        name: String,
        value: String
    ) {
        try {
            val fields = this.javaClass.fields
            for (i in fields.indices) {
                if (parseAndSetField(fields[i], name, value)) return
            }
        } catch (e: Exception) {
            Log.e("ttt", "[setAttribute]:${e}")
        }
    }

    private fun parseAndSetField(field: Field, name: String, value: String): Boolean {
        try {
            val animNodeName =
                javaClass.getAnnotation(AnimNodeName::class.java)?.name ?: return false
            val annotation = field.getAnnotation(AnimAttributeName::class.java) ?: return false
            val key = "${animNodeName}_${annotation.name}"
            if (annotation.name.equals(name, true)) {
                AnimDecoder2.mapNodeAttributeCoderMap[key]?.let {
                    it.attributeDecode(field.type.kotlin, value)?.let {
                        field.set(this, it)
                    }
                } ?: kotlin.run {
                    val attributeCoder = annotation.coder.java.newInstance()
                    AnimDecoder2.mapNodeAttributeCoderMap[key] = attributeCoder
                    attributeCoder.attributeDecode(field.type.kotlin, value)?.let {
                        field.set(this, it)
                    }
                }
                Log.i("zzc", "parseAndSetField ${annotation.name} $name $value ${field.get(this)}")
                return true
            }
        } catch (e: Exception) {
            Log.e("ttt", "[parseAndSetField]:${e}")
        }
        return false
    }

    fun getCenterOfViewLocationInWindow(view: View): IntArray {
        val pos = IntArray(2)
        view.getLocationInWindow(pos)
        pos[1] = pos[1] - getStatusBarHeight()
        pos[0] = (pos[0] + view.width / 2f).toInt()
        pos[1] = (pos[1] + view.height / 2f).toInt()
        return pos
    }

    @SuppressLint("InternalInsetResource")
    private fun getStatusBarHeight(): Int {
        var height = 0
        val resourceId: Int = AnimationEx.mApplication.resources.getIdentifier(
            "status_bar_height",
            "dimen",
            "android"
        )
        if (resourceId > 0) {
            height = AnimationEx.mApplication.resources.getDimensionPixelSize(resourceId)
        }
        return height
    }
}