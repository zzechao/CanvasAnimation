package com.base.canvasanimation

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.base.animation.BitmapLoader
import com.base.animation.DisplayObject
import com.base.animation.IAnimListener
import com.base.animation.IClickIntercept
import com.base.animation.item.BitmapDisplayItem
import com.base.animation.model.AnimDrawObject
import com.base.animation.model.AnimPathObject
import com.base.animation.model.PathObject
import com.base.animation.xml.AnimDecoder
import com.base.animation.xml.AnimDecoder2
import com.base.animation.xml.AnimEncoder
import com.base.animation.xml.buildAnimNode
import com.base.animation.xml.buildString
import com.base.animation.xml.node.coder.InterpolatorEnum
import kotlinx.android.synthetic.main.fragment_anim_canvas.anim_1
import kotlinx.android.synthetic.main.fragment_anim_canvas.anim_2
import kotlinx.android.synthetic.main.fragment_anim_canvas.anim_3
import kotlinx.android.synthetic.main.fragment_anim_canvas.anim_surface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * @author:zhouzechao
 * @date: 2020/11/17
 * description：
 */
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class TestAnimCanvasFragment : Fragment(), IClickIntercept, IAnimListener {

    private val xml = "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\n" +
            "<anim>\n" +
            "    <imageNode displaySize=\"80\" url=\"https://turnover-cn.oss-cn-hangzhou.aliyuncs.com/turnover/1670379863915_948.png\">\n" +
            "        <startAnim alpha=\"255\" startIdName=\"\" startL='{\"x\":0.0,\"y\":0.0}' rotation=\"0.0\" scaleX=\"0.5\" scaleY=\"0.5\">\n" +
            "            <endAnim alpha=\"255\" durTime=\"1000\" interpolator=\"1\" endIdName=\"\" endL='{\"x\":680.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"2.0\" scaleY=\"2.0\" url=\"\" />\n" +
            "            <txtNode txtColor=\"#ff0000ff\" fontSize=\"40\" txt=\"测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据\">\n" +
            "                <startAnim alpha=\"255\" startIdName=\"\" startL='{\"x\":680.0,\"y\":40.0}' rotation=\"0.0\" scaleX=\"1.0\" scaleY=\"1.0\">\n" +
            "                    <endAnim alpha=\"255\" durTime=\"5000\" interpolator=\"1\" endIdName=\"\" endL='{\"x\":0.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"1.0\" scaleY=\"1.0\" url=\"\" />\n" +
            "                </startAnim>\n" +
            "            </txtNode>\n" +
            "            <layoutNode data=\"\" layoutIdName=\"view_test_layout\" versionCode=\"version_1.0\">\n" +
            "                <endAnim alpha=\"255\" durTime=\"1000\" interpolator=\"0\" endIdName=\"\" endL='{\"x\":680.0,\"y\":2967.0}' rotation=\"0.0\" scaleX=\"2.0\" scaleY=\"2.0\" url=\"\" />\n" +
            "            </layoutNode>\n" +
            "            <endAnim alpha=\"255\" durTime=\"1000\" interpolator=\"2\" endIdName=\"\" endL='{\"x\":1400.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"2.0\" scaleY=\"2.0\" url=\"\" />\n" +
            "            <layoutNode data=\"\" layoutIdName=\"view_test_layout\" versionCode=\"version_1.0\">\n" +
            "                <endAnim alpha=\"255\" durTime=\"1000\" interpolator=\"0\" endIdName=\"\" endL='{\"x\":680.0,\"y\":40.0}' rotation=\"0.0\" scaleX=\"0.0\" scaleY=\"0.0\" url=\"\" />\n" +
            "            </layoutNode>\n" +
            "        </startAnim>\n" +
            "    </imageNode>\n" +
            "    <layoutNode data=\"\" layoutIdName=\"view_test_layout\" versionCode=\"version_1.0\">\n" +
            "        <startAnim alpha=\"255\" startIdName=\"\" startL='{\"x\":680.0,\"y\":40.0}' rotation=\"0.0\" scaleX=\"0.0\" scaleY=\"0.0\">\n" +
            "            <endAnim alpha=\"255\" durTime=\"1000\" interpolator=\"1\" endIdName=\"\" endL='{\"x\":0.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"3.0\" scaleY=\"3.0\" url=\"\" />\n" +
            "        </startAnim>\n" +
            "    </layoutNode>\n" +
            "</anim>\n"

    private val xmlMore = "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\n" +
            "<anim>\n" +
            "    <startAnim alpha=\"255\" displaySize=\"80\" startId=\"0\" startL='{\"x\":0.0,\"y\":0.0}' rotation=\"0.0\" scaleX=\"0.5\" scaleY=\"0.5\" url=\"https://turnover-cn.oss-cn-hangzhou.aliyuncs.com/turnover/1670379863915_948.png\">\n" +
            "        <endAnim alpha=\"255\" displaySize=\"0\" durTime=\"1000\" interpolator=\"2\" endId=\"0\" endL='{\"x\":680.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"2.0\" scaleY=\"2.0\" url=\"\" />\n" +
            "        <endContainer displaySize=\"0\" durTime=\"1500\" url=\"\">\n" +
            "            <endAnim alpha=\"255\" displaySize=\"0\" durTime=\"1000\" interpolator=\"0\" endId=\"0\" endL='{\"x\":680.0,\"y\":0.0}' rotation=\"360.0\" scaleX=\"1.0\" scaleY=\"1.0\" url=\"\" />\n" +
            "            <endAnim alpha=\"0\" displaySize=\"0\" durTime=\"1000\" interpolator=\"0\" endId=\"0\" endL='{\"x\":0.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"1.0\" scaleY=\"1.0\" url=\"\" />\n" +
            "            <endAnim alpha=\"255\" displaySize=\"0\" durTime=\"1000\" interpolator=\"0\" endId=\"0\" endL='{\"x\":680.0,\"y\":3007.0}' rotation=\"0.0\" scaleX=\"0.0\" scaleY=\"0.0\" url=\"\" />\n" +
            "            <endAnim alpha=\"255\" displaySize=\"0\" durTime=\"1000\" interpolator=\"0\" endId=\"0\" endL='{\"x\":1440.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"0.0\" scaleY=\"0.0\" url=\"\" />\n" +
            "        </endContainer>\n" +
            "    </startAnim>\n" +
            "</anim>"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_anim_canvas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        anim_1?.setOnClickListener {
            lifecycleScope.launch {
                repeat(50) {
                    startSingleAnim2()
                    delay(200)
                }
            }
        }

        anim_2?.setOnClickListener {
            startMoreAnim2()
        }

        anim_3?.setOnClickListener {
            startAnimRain()
        }
        anim_surface?.addAnimListener(this)
        anim_surface?.addClickIntercept(this)
    }

    override fun onResume() {
        super.onResume()
        anim_surface?.resume()
    }

    override fun onPause() {
        super.onPause()
        anim_surface?.pause()
    }

    private fun startSingleAnim2() {
        val size = 80
        val url = "https://turnover-cn.oss-cn-hangzhou.aliyuncs.com/turnover/1670379863915_948.png"
        AnimEncoder().buildAnimNode {
            imageNode {
                this.url = url
                this.displayHeightSize = size
                startNode {
                    point = PointF(0f, 0f)
                    scaleX = 0.5f
                    scaleY = 0.5f
                    endNode {
                        point = PointF(
                            DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / 2,
                            DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / 2
                        )
                        scaleX = 2f
                        scaleY = 2f
                        durTime = 1000
                        interpolator = InterpolatorEnum.Accelerate.type
                    }
                    txtNode {
                        this.txt = "测试数据"
                        this.color = Color.BLUE
                        this.fontSize = 20
                        startNode {
                            point = PointF(
                                DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                    .toFloat() / 2 - size / 2,
                                size / 2f
                            )
                            scaleX = 1f
                            scaleY = 1f
                            endNode {
                                point = PointF(
                                    0f,
                                    DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                        .toFloat() / 2 - size / 2
                                )
                                scaleX = 3f
                                scaleY = 3f
                                durTime = 2000
                                interpolator = InterpolatorEnum.Accelerate.type
                            }

                        }
                    }
                    layoutNode {
                        this.layoutIdName = "view_test_layout"
                        endNode {
                            point = PointF(
                                DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                    .toFloat() / 2 - size / 2,
                                size / 2f
                            )
                            scaleX = 1f
                            scaleY = 1f
                            durTime = 2000
                            interpolator = InterpolatorEnum.Accelerate.type
                        }
                        endNode {
                            point = PointF(
                                DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                    .toFloat() / 2 - size / 2,
                                DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                    .toFloat() - size / 2
                            )
                            scaleX = 2f
                            scaleY = 2f
                            durTime = 1000
                            interpolator = InterpolatorEnum.Linear.type
                        }
                    }
                    endNode {
                        point = PointF(
                            DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                .toFloat() - size / 2,
                            DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / 2
                        )
                        scaleX = 2f
                        scaleY = 2f
                        durTime = 1000
                        interpolator = InterpolatorEnum.Decelerate.type
                    }
                    layoutNode {
                        this.layoutIdName = "view_test_layout"
                        endNode {
                            point = PointF(
                                DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                    .toFloat() / 2 - size / 2,
                                size / 2f
                            )
                            scaleX = 0f
                            scaleY = 0f
                            durTime = 1000
                            interpolator = InterpolatorEnum.Linear.type
                        }
                    }
                }
            }
            layoutNode {
                this.layoutIdName = "view_test_layout"
                startNode {
                    point = PointF(
                        DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                            .toFloat() / 2 - size / 2,
                        size / 2f
                    )
                    scaleX = 0f
                    scaleY = 0f
                    endNode {
                        point = PointF(
                            0f,
                            DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / 2
                        )
                        scaleX = 3f
                        scaleY = 3f
                        durTime = 1000
                        interpolator = InterpolatorEnum.Accelerate.type
                    }
                }
            }
        }.apply {
            Log.i("tttt2", this.buildString())
            lifecycleScope.launch(Dispatchers.IO) {
                AnimDecoder2.suspendPlayAnimWithAnimNode(
                    anim_surface,
                    this@apply,
                ) { node, displayItem ->
                    when (displayItem) {
                        is BitmapDisplayItem -> {
                            displayItem.mBitmap =
                                BitmapLoader.decodeBitmapFrom(resources, R.mipmap.xin, 1, 100, 100)
                        }
                    }
                    displayItem
                }
            }
        }
    }


    private fun startSingleAnim3() {
        lifecycleScope.launch(Dispatchers.IO) {
            AnimDecoder2.suspendPlayAnimWithXml(anim_surface, xml) { node, displayItem ->
                when (displayItem) {
                    is BitmapDisplayItem -> {
                        displayItem.mBitmap =
                            BitmapLoader.decodeBitmapFrom(resources, R.mipmap.xin, 1, 100, 100)
                    }
                }
                displayItem
            }
        }
    }


    private fun startMoreAnim3() {
        lifecycleScope.launch(Dispatchers.IO) {
            AnimDecoder.suspendPlayAnimWithXml(xmlMore, anim_surface, xmlMore) {
                BitmapFactory.decodeResource(resources, R.mipmap.xin)
            }
        }
    }

    private fun startMoreAnim2() {
        val size = 80
        val url = "https://turnover-cn.oss-cn-hangzhou.aliyuncs.com/turnover/1670379863915_948.png"
        AnimEncoder().buildAnimNode {
            imageNode {
                this.url = url
                this.displayHeightSize = size
                startNode {
                    point = PointF(0f, 0f)
                    scaleX = 0.5f
                    scaleY = 0.5f
                    endNode {
                        point = PointF(
                            DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / 2,
                            DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / 2
                        )
                        scaleX = 2f
                        scaleY = 2f
                        durTime = 1000
                        interpolator = InterpolatorEnum.Decelerate.type
                    }
                    endContainer {
                        durTime = 3000
                        endNode {
                            durTime = 1000
                            rotation = 360f
                            point = PointF(
                                DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                    .toFloat() / 2 - size / 2,
                                0f
                            )
                        }
                        endNode {
                            point = PointF(
                                0f,
                                DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                    .toFloat() / 2 - size / 2
                            )
                            alpha = 0
                        }
                        endNode {
                            point = PointF(
                                DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                    .toFloat() / 2 - size / 2,
                                DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                    .toFloat()
                            )
                            scaleX = 0f
                            scaleY = 0f
                        }
                        layoutNode {
                            this.layoutIdName = "view_test_layout"
                            endNode {
                                point = PointF(
                                    DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                        .toFloat(),
                                    DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                        .toFloat() / 2 - size / 2
                                )
                                alpha = 0
                                rotation = 360f
                                scaleX = 0f
                                scaleY = 0f
                            }
                        }
                    }
                }
            }
        }.apply {
            Log.i("tttt2", this.buildString())
            lifecycleScope.launch {
                AnimDecoder2.suspendPlayAnimWithAnimNode(
                    anim_surface,
                    this@apply
                ) { node, displayItem ->
                    when (displayItem) {
                        is BitmapDisplayItem -> {
                            displayItem.mBitmap =
                                BitmapLoader.decodeBitmapFrom(resources, R.mipmap.xin, 1, 100, 100)
                        }
                    }
                    displayItem
                }
            }
        }
    }

    /**
     * 动画雨
     */
    private fun startAnimRain() {
        val size = 80
        val displayObject = DisplayObject.with()
        val displayItemId = displayObject.add(
            key = "xin_startMoreAnim", kClass = BitmapDisplayItem::class
        ) {
            val bitmap =
                BitmapLoader.decodeBitmapFrom(resources, R.mipmap.xin, 1, size, size)
            val bitmapWidth = bitmap.width
            val bitmapHeight = bitmap.height
            val displayWidth = size * bitmapWidth / bitmapHeight
            return@add BitmapDisplayItem().apply {
                mBitmap = bitmap
                setDisplaySize(displayWidth, size)
            }
        }
        val width = DisplayUtils.getScreenWidth(this.activity).toFloat()
        val indexSize = (width / 80 / 2).toInt()

        for (i in 0 until indexSize) {
            val start =
                PathObject(
                    displayItemId,
                    point = PointF((i * 80 + (80 * (i + 1))).toFloat(), 0f),
                    interpolator = LinearInterpolator(),
                    scaleX = 1f,
                    scaleY = 1f
                )
            val next =
                PathObject(
                    displayItemId,
                    point = PointF(
                        (i * 80 + (80 * (i + 1))).toFloat(),
                        DisplayUtils.getScreenHeight(this.activity).toFloat()
                    ),
                    interpolator = LinearInterpolator(),
                    scaleX = 1f,
                    scaleY = 1f
                )
            val time = (1000L..10000L).random()
            anim_surface?.addAnimDisplay(
                AnimPathObject.Inner.with().beginAnimPath(start)
                    .doAnimPath(time, next).build(displayObject.build())
                    .apply {
                        clickable = true
                        expand = "rain"
                    })
        }
    }

    override fun intercept(animId: Long, animDrawObject: AnimDrawObject, touchPointF: PointF) {
        Toast.makeText(this@TestAnimCanvasFragment.context, "$animId", Toast.LENGTH_SHORT).show()
        anim_surface?.removeAnimId(animId)
        //startSingleAnim()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        anim_surface?.endAnimation()
        anim_surface?.removeAnimListener(this)
        anim_surface?.removeClickIntercept(this)
    }

    override fun startAnim(animId: Long) {
    }

    override fun runningAnim(animId: Long) {
    }

    override fun endAnim(animId: Long) {
    }
}