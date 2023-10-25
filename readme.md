# CanvasAnimation

## 介绍

`CanvasAnimation` 是一个轻量的属性动画的库。支持普通的view以及surfaceview两种布局进行绘制，还支持xml远程配置模式，配置方便。
`CanvasAnimation` 使用原生 Android Canvas 库渲染动画，为你提供高性能、低开销的动画体验。

## 效果图

### 做类似的动画效果（间隔50ms播放100个动画）

[![Watch the video](https://raw.github.com/GabLeRoux/WebMole/master/ressources/WebMole_Youtube_Video.png)](https://github.com/zzechao/CanvasAnimation/blob/canvas_view_new_feature_2.0/OScreen_recording.mp4)

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
