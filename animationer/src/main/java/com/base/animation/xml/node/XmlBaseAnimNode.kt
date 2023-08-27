package com.base.animation.xml.node

interface XmlBaseAnimNode {
    /**
     * 设置属性时调用
     * @param name 属性名，如：url，name
     * @param value 属性值，如xxx
     */
    fun setAttribute(name: String, value: String)

    /**
     * 创建子节点时调用
     * @param name 标签名，如tag1，tag2
     */
    fun createChildNode(name: String): XmlBaseAnimNode?

    /**
     * 子节点添加到父时调用
     * 如：创建了tag2 的Node，添加到tag1中
     */
    fun addNode(obj: XmlBaseAnimNode) {}

    /**
     * <ACB>text</ABC>
     */
    fun setText(text: String)
}