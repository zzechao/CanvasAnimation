# CanvasAnimation

## 介绍

`CanvasAnimation` 是一个轻量的属性动画的库。支持普通的view以及surfaceview两种布局进行绘制，还支持xml远程配置模式，配置方便。
`CanvasAnimation` 使用原生 Android Canvas 库渲染动画，为你提供高性能、低开销的动画体验。

## 效果图

### 做类似的动画效果（间隔50ms播放100个动画）
![image](https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/OScreen_recording.gif)

### 用普通属性动画，内存消耗情况
![image](https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/origin.jpg)

### 使用canvas进行动画绘制,总体只消耗5M内存，比普通的属性动画优化43M内存
![image](https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/optimized.jpg)


## 用法

clone 项目，自行导出aar库，（之后提供build.gradle支持）

### 动画库初始化

```kotlin
    /**
     * application 应用的Application
     * displayMaxCacheSize 缓存display的大小（这里有DisplayItem的重用逻辑，根据内部声称key）
     * mode 模式1：计算动画节点预先处理，模式2：计算动画节点，根据每帧时长实时计算
     */
    AnimationEx.init(this.application, 200, 2)
```

### 构建动画的节点（代码构建）
```kotlin
    /**
     * 代码创建一个绘制节点整个过程，AnimEncoder编码器；
     * imageNode是图片的节点（除了imageNode还有TextNode、layoutNode、自定义的Node的DisplayItem，可以理解为绘制的元素）；
     * startNode是开始节点（坐标有两种定义方式，一个是根据layout的id对应res中ids的name获取）；
     * endNode结束节点（和startNode类似）；
     * 还有一种情况是一开始节点，多个结束节点，类似送礼同时分散到多个麦位的，可以用endNodeContainer包含多个endNode
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

## 执行方式 
```kotlin 
    /**
     * 代码构建节点通过这个方式直接运行；
     * 调用有个闭包的回调方式，用来做上层修改绘制元素的变量设置以及特定拦截操作，Glide加载，
     * 以及通过一些请求返回的数据修改layout里的字段等等，采用挂起协程的方式进行
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

### xml的方式构建节点（远端返回对应字符串进行解析） 
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

## 执行方式
```kotlin 
    /**
     * 与代码构建节点调用方式类似
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

### 动画的效果演示
<iframe
src="https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/video1.mp4"
scrolling="no"
border="0"
frameborder="no"
framespacing="0"
allowfullscreen="true"
height=500
width=250>
</iframe>

<video id="video" controls="" preload="none" poster="封面">
      <source id="mp4" src="https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/video2.mp4" type="video/mp4">
</videos>

### 提供自定义的绘制元素节点以及路径过程节点，效果图
<video id="video" controls="" preload="none" poster="封面">
      <source id="mp4" src="https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/video3.mp4" type="video/mp4">
</videos>

可以参考BitmapDouDisplay、ImageDouNode