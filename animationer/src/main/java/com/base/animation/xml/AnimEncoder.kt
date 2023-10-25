package com.base.animation.xml

import com.base.animation.Animer
import com.base.animation.node.AnimNode
import com.base.animation.node.EndNode
import com.base.animation.node.EndNodeContainer
import com.base.animation.node.ImageNode
import com.base.animation.node.LayoutNode
import com.base.animation.node.StartNode
import com.base.animation.node.TextNode
import com.base.animation.xml.node.XmlBaseAnimNode

/**
 * @author:zhouzechao
 * description：*
 */
private const val TAG = "AnimEncoder"

class AnimEncoder {

    val rootNode: AnimNode by lazy {
        AnimNode()
    }
    var curNode: XmlBaseAnimNode = rootNode

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

    fun endContainer(onInit: EndNodeContainer.(encoder: AnimEncoder) -> Unit) {
        curNode.addNode(EndNodeContainer().apply {
            val lastNode = curNode
            try {
                curNode = this
                onInit(this, this@AnimEncoder)
            } finally {
                curNode = lastNode
            }
        })
    }

    fun imageNode(onInit: ImageNode.(encoder: AnimEncoder) -> Unit) {
        curNode.addNode(ImageNode().apply {
            val lastNode = curNode
            try {
                curNode = this
                onInit(this, this@AnimEncoder)
            } finally {
                curNode = lastNode
            }
        })
    }

    fun txtNode(onInit: TextNode.(encoder: AnimEncoder) -> Unit){
        curNode.addNode(TextNode().apply {
            val lastNode = curNode
            try {
                curNode = this
                onInit(this, this@AnimEncoder)
            } finally {
                curNode = lastNode
            }
        })
    }

    fun layoutNode(onInit: LayoutNode.(encoder: AnimEncoder) -> Unit){
        curNode.addNode(LayoutNode().apply {
            val lastNode = curNode
            try {
                curNode = this
                onInit(this, this@AnimEncoder)
            } finally {
                curNode = lastNode
            }
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
        Animer.log.e(TAG, "[buildRichTextXmlString]:${e}")
        ""
    }
}

fun AnimEncoder.buildAnimNode(onInit: AnimEncoder.() -> Unit): AnimNode {
    val encoder = this
    onInit(encoder)
    return rootNode
}

fun AnimNode.buildString(): String {
    return XmlWriterHelper.newString {
        this@buildString.encode(this)
    }
}