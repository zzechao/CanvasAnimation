package com.base.animation.xml

import android.util.Log
import com.base.animation.node.AnimNode
import com.base.animation.node.EndNode
import com.base.animation.node.StartNode
import com.base.animation.xml.node.XmlBaseAnimNode

private const val TAG = "AnimEncoder"

class AnimEncoder {

    val rootNode: AnimNode by lazy {
        AnimNode()
    }
    private var curNode: XmlBaseAnimNode = rootNode

    fun startNode(onInit: StartNode.(encoder: AnimEncoder) -> Unit) {
        curNode.addNode(StartNode().apply {
            val lastNode = curNode
            try {
                curNode = this
                onInit(this, this@AnimEncoder)
            } finally {
                curNode = lastNode
            }
        })
    }

    fun endNode(onInit: EndNode.() -> Unit) {
        curNode.addNode(EndNode().apply {
            onInit(this)
        })
    }
}

fun AnimEncoder.buildAnimXmlString(onInit: AnimEncoder.() -> Unit): String {
    return try {
        val encode = this
        onInit(encode)
        XmlWriterHelper.newString {
            encode.rootNode.encode(this)
        }
    } catch (e: Exception) {
        Log.e(TAG, "[buildRichTextXmlString]:${e}")
        ""
    }
}

fun AnimEncoder.buildAnimNode(onInit: AnimEncoder.() -> Unit): AnimNode {
    val encoder = this
    onInit(encoder)
    return rootNode
}