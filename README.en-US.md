# CanvasEGLAnimation

[![GitHub license](https://img.shields.io/github/license/JailedBird/ArouterGradlePlugin.svg)](https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/LICENSE)

## Introduction

`CanvasEGLAnimation` uses a declarative approach to build animation nodes, supports custom node drawing elements and path process nodes, supports multiple interpolators, and supports XML remote configuration mode for convenient configuration.
`CanvasEGLAnimation` uses the native Android Canvas library to render animations, providing you with a high-performance, low-overhead animation experience.
`CanvasEGLAnimation` version 1.1.0 supports EGLTextureView independent thread queue rendering, adopting GL rendering to complement the shortcomings of SurfaceView for animation usage.

## Effect Images

### Creating similar animation effects (playing 100 animations at 50ms intervals)
![image](https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/OScreen_recording.gif)

### Memory consumption using ordinary property animations
![image](https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/origin.jpg)

### Using canvas for animation drawing, total memory consumption is only 5MB, optimizing 43MB memory compared to ordinary property animations
![image](https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/optimized.jpg)


## Usage
Add `mavenCentral()` in the root-level `build.gradle`

```groovy
    repositories {
        mavenCentral()
    }
```

Add to the model-level `build.gradle`

```groovy
     implementation "io.github.zzechao:canvasanimation:1.1.0"
```

Current version: 1.1.0

### Animation Library Initialization

```kotlin
    /**
     * @param application The Application of the app
     * @param displayMaxCacheSize Cache size for display (there is a reuse logic for DisplayItem, based on internally generated keys)
     * @param mode Mode 1: Pre-process animation nodes, Mode 2: Calculate animation nodes, compute in real-time per frame
     * @param nodeClazzs Decoder node registration (node classes); if XML uses custom displayItem nodes, they must be registered first so the node tree can be parsed
     */
    AnimationEx.init(this.application, 200, 2, ImageBezierNode::class.java, ImageDouNode::class.java)

    /**
     * @param nodeClazz - Node class Decoder node registration (node class); if XML uses custom displayItem nodes, they must be registered first so the node tree can be parsed
     */
    AnimationEx.registerNode(ImageDouNode::class.java)
```

### Building Animation Nodes (Code-based Construction)
```kotlin
    /**
     * Code-based creation of a drawing node, the entire process; AnimEncoder is the encoder;
     * imageNode is the image node (besides imageNode there are TextNode, layoutNode, custom Node's DisplayItem, which can be understood as the drawing element);
     * startNode is the start node (coordinates have two definition methods: one is based on the layout id corresponding to the res id name);
     * endNode is the end node (similar to startNode);
     * There is also a case where there is one start node and multiple end nodes, like gifting simultaneously to multiple positions, which can use endNodeContainer to contain multiple endNodes
     */
    val animNode = AnimEncoder().buildAnimNode {
            imageNode {
                this.url = url
                this.displayHeightSize = size
                startNode {
                    layoutIdName = "test1"
                    point = PointF(0f, 0f)
                    scaleX = 0.5f
                    scaleY = 0.5f
                    endNode {
                        layoutIdName = "test2"
                        point = PointF(100f, 100f)
                        scaleX = 3f
                        scaleY = 3f
                        durTime = 1000
                        interpolator = InterpolatorEnum.Accelerate.type
                    }
                    endNode {
                        layoutIdName = "test3"
                        point = PointF(100f, 0f)
                        scaleX = 0.5f
                        scaleY = 0.5f
                        durTime = 2000
                        interpolator = InterpolatorEnum.Accelerate.type
                    }
                }
            }
        }
```

## Execution Methods
```kotlin 
    /**
     * For code-built nodes, use this method to run directly;
     * There is a closure callback method for calling, used for upper-layer modification of drawing element variable settings and specific interception operations, Glide loading,
     * as well as modifying fields in layouts through data returned from certain requests, using suspended coroutine approach
     */
    AnimDecoder2.suspendPlayAnimWithAnimNode(
        anim_surface,
        animNode,
    ) { node, displayItem ->
        when (displayItem) {
            is BitmapDisplayItem -> {
                displayItem.mBitmap =
                    BitmapLoader.decodeBitmapFrom(resources, R.mipmap.xin, 1, 100, 100)
            }
        }
        displayItem
    }
```

### Building nodes via XML (parsing corresponding strings returned remotely)
```xml 
    <?xml version='1.0' encoding='utf-8' standalone='yes' ?>
    <anim>
        <imageNode displaySize="80" url="https://turnover-cn.oss-cn-hangzhou.aliyuncs.com/turnover/1670379863915_948.png">
            <startAnim alpha="255" startIdName="test1" startL='{"x":0.0,"y":0.0}' rotation="0.0" scaleX="0.5" scaleY="0.5">
                <endAnim alpha="255" durTime="1000" interpolator="1" endIdName="test2" endL='{"x":100.0,"y":100.0}' rotation="0.0" scaleX="3.0" scaleY="3.0" url="" />
                <endAnim alpha="255" durTime="2000" interpolator="1" endIdName="test3" endL='{"x":100.0,"y":0.0}' rotation="0.0" scaleX="0.5" scaleY="0.5" url="" />
            </startAnim>
        </imageNode>
    </anim>
```

## Execution Methods
```kotlin 
    /**
     * Similar to the code-built node calling method
     */
    AnimDecoder2.suspendPlayAnimWithXml(anim_surface, xml) { node, displayItem ->
        when (displayItem) {
            is BitmapDisplayItem -> {
                displayItem.mBitmap =
                    BitmapLoader.decodeBitmapFrom(resources, R.mipmap.xin, 1, 100, 100)
            }
        }
        displayItem
    }
```

### Animation effect demonstration; compressing to GIF may cause quality loss; you can refer to the mp4 in the project for clearer and smoother visuals
![image](https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/v1.gif)

![image](https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/v2.gif)

### Provides custom drawing element nodes and path process nodes, effect images
![image](https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/v3.gif)

### Alipay-style red packet rain effect
![image](https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/v4.gif)

### Custom nodes and ItemDisplay drawing
```kotlin 
/**
 * Custom declarative node
 */
class ImageDouNode : ImageNode(), IXmlDrawableNodeDealIntercept {

    @AnimAttributeName("rocation", DefaultAttributeCoder::class)
    @JvmField
    var rocation = 5

    override var displayItem: KClass<out BaseDisplayItem> = BitmapDouDisplay::class

    override val dealIntercept: IDealNodeDealIntercept = object : IDealNodeDealIntercept {
        override suspend fun invoke(
            displayObject: DisplayObject,
            animNode: IAnimNode,
            chain: AnimNodeChain,
            dealDisplayItem: DealDisplayItem
        ): String {
            if (animNode is ImageDouNode) {
                val key =
                    animNode.url + animNode.displayHeightSize + animNode.nodeName
                val bitmapKey = animNode.url + animNode.displayHeightSize + animNode.nodeName
                val displayId = displayObject.suspendAdd(
                    key = key, kClass = animNode.displayItem
                ) {
                    val bitmapDisplayItem = BitmapDouDisplay(rocation)
                    dealDisplayItem.invoke(animNode, bitmapDisplayItem) // Delegate to handle image loading method
                    val bitmapWidth = bitmapDisplayItem.mBitmap?.width ?: return@suspendAdd null
                    val bitmapHeight = bitmapDisplayItem.mBitmap?.height ?: return@suspendAdd null
                    val displayWidth = animNode.displayHeightSize * bitmapWidth / bitmapHeight
                    bitmapDisplayItem.setDisplaySize(displayWidth, animNode.displayHeightSize)
                    bitmapDisplayItem
                }
                return displayId
            }
            return ""
        }
    }

    /**
     * Custom drawing node calculation and drawing process
     */
    inner class BitmapDouDisplay(val rocation: Int) : BitmapDisplayItem() {

        override var isCalculate: Boolean = true

        override fun calculate(pathProcess: PathProcess, current: AnimDrawObject, interpolator: BaseInterpolator) {
            super.calculate(pathProcess, current, interpolator)
            val p = pathProcess.curTotalTime / pathProcess.durTime
            val interP = pathProcess.interpolator.getInterpolation(p)
            val inPoint = PointF(
                pathProcess.start.point.x + pathProcess.item.totalX * interP,
                pathProcess.start.point.y + pathProcess.item.totalY * interP
            )
            val alpha = pathProcess.start.alpha + (pathProcess.item.totalAlpha * interP).toInt()
            val scaleX = pathProcess.start.scaleX + pathProcess.item.totalScaleX * interP
            val scaleY = pathProcess.start.scaleY + pathProcess.item.totalScaleY * interP
            val rotation = sin(pathProcess.curTotalTime / 30L) * rocation
            current.reset(inPoint, alpha, scaleX, scaleY, rotation)
        }
    }
}

/**
 * AnimEncoder extension for ImageDouNode node, constructing declarative method
 */
fun AnimEncoder.imageDouNode(onInit: ImageDouNode.(encoder: AnimEncoder) -> Unit) {
    curNode.addNode(ImageDouNode().apply {
        val lastNode = curNode
        try {
            curNode = this
            onInit(this, this@imageDouNode)
        } finally {
            curNode = lastNode
        }
    })
}

/**
 * Constructing animation for custom drawing itemDisplay nodes
 */
AnimEncoder().buildAnimNode {
    imageDouNode { // Custom drawing node
        this.rocation = 5
        this.url = url
        this.displayHeightSize = size
        startNode {
            scaleX = 2f
            scaleY = 2f
            point = PointF(
                DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context).toFloat() / 2 - size / scaleX / 2,
                DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context).toFloat() - size / scaleY / 2)
            endNode {
                scaleX = 2f
                scaleY = 2f
                point = PointF(DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context).toFloat() / 2 - size / scaleX / 2,
                    DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context).toFloat() / 2 - size / scaleY / 2)
                durTime = 1000
                interpolator = InterpolatorEnum.Decelerate.type
            }
            endNode {
                scaleX = 2f
                scaleY = 2f
                point = PointF(DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context).toFloat() / 2 - size / scaleX / 2,
                    DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context).toFloat() - size / scaleY / 2)
                durTime = 2000
                interpolator = InterpolatorEnum.Accelerate.type
            }
        }
    }
}
```

You can refer to BitmapDouDisplay, ImageDouNode
