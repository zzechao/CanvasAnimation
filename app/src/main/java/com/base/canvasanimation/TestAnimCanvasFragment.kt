package com.base.canvasanimation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.base.animation.Animer
import com.base.animation.BitmapLoader
import com.base.animation.DisplayObject
import com.base.animation.IAnimListener
import com.base.animation.IClickIntercept
import com.base.animation.item.BitmapDisplayItem
import com.base.animation.item.LayoutDisplayItem
import com.base.animation.model.AnimDrawObject
import com.base.animation.model.AnimPathObject
import com.base.animation.model.PathObject
import com.base.animation.node.ImageNode
import com.base.animation.xml.AnimDecoder2
import com.base.animation.xml.AnimEncoder
import com.base.animation.xml.buildAnimNode
import com.base.animation.xml.buildString
import com.base.animation.xml.node.coder.InterpolatorEnum
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.fragment_anim_canvas.anim_1
import kotlinx.android.synthetic.main.fragment_anim_canvas.anim_2
import kotlinx.android.synthetic.main.fragment_anim_canvas.anim_3
import kotlinx.android.synthetic.main.fragment_anim_canvas.anim_surface
import kotlinx.android.synthetic.main.fragment_anim_canvas.relative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


/**
 * @author:zhouzechao
 * @date: 2020/11/17
 * description：
 */
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class TestAnimCanvasFragment : Fragment(), IClickIntercept, IAnimListener {

    private val xml =
        "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\n" + "<anim>\n" + "    <imageNode displaySize=\"80\" url=\"https://turnover-cn.oss-cn-hangzhou.aliyuncs.com/turnover/1670379863915_948.png\">\n" + "        <startAnim alpha=\"255\" startIdName=\"\" startL='{\"x\":0.0,\"y\":0.0}' rotation=\"0.0\" scaleX=\"0.5\" scaleY=\"0.5\">\n" + "            <endAnim alpha=\"255\" durTime=\"1000\" interpolator=\"1\" endIdName=\"\" endL='{\"x\":680.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"2.0\" scaleY=\"2.0\" url=\"\" />\n" + "            <txtNode txtColor=\"#ff0000ff\" fontSize=\"40\" txt=\"测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据测试数据\">\n" + "                <startAnim alpha=\"255\" startIdName=\"\" startL='{\"x\":680.0,\"y\":40.0}' rotation=\"0.0\" scaleX=\"1.0\" scaleY=\"1.0\">\n" + "                    <endAnim alpha=\"255\" durTime=\"5000\" interpolator=\"1\" endIdName=\"\" endL='{\"x\":0.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"1.0\" scaleY=\"1.0\" url=\"\" />\n" + "                </startAnim>\n" + "            </txtNode>\n" + "            <layoutNode data=\"\" layoutIdName=\"view_test_layout\" versionCode=\"version_1.0\">\n" + "                <endAnim alpha=\"255\" durTime=\"1000\" interpolator=\"0\" endIdName=\"\" endL='{\"x\":680.0,\"y\":2967.0}' rotation=\"0.0\" scaleX=\"2.0\" scaleY=\"2.0\" url=\"\" />\n" + "            </layoutNode>\n" + "            <endAnim alpha=\"255\" durTime=\"1000\" interpolator=\"2\" endIdName=\"\" endL='{\"x\":1400.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"2.0\" scaleY=\"2.0\" url=\"\" />\n" + "            <layoutNode data=\"\" layoutIdName=\"view_test_layout\" versionCode=\"version_1.0\">\n" + "                <endAnim alpha=\"255\" durTime=\"1000\" interpolator=\"0\" endIdName=\"\" endL='{\"x\":680.0,\"y\":40.0}' rotation=\"0.0\" scaleX=\"0.0\" scaleY=\"0.0\" url=\"\" />\n" + "            </layoutNode>\n" + "        </startAnim>\n" + "    </imageNode>\n" + "    <layoutNode data=\"\" layoutIdName=\"view_test_layout\" versionCode=\"version_1.0\">\n" + "        <startAnim alpha=\"255\" startIdName=\"\" startL='{\"x\":680.0,\"y\":40.0}' rotation=\"0.0\" scaleX=\"0.0\" scaleY=\"0.0\">\n" + "            <endAnim alpha=\"255\" durTime=\"1000\" interpolator=\"1\" endIdName=\"\" endL='{\"x\":0.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"3.0\" scaleY=\"3.0\" url=\"\" />\n" + "        </startAnim>\n" + "    </layoutNode>\n" + "</anim>\n"

    private val xmlMore =
        "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\n" + "<anim>\n" + "    <startAnim alpha=\"255\" displaySize=\"80\" startId=\"0\" startL='{\"x\":0.0,\"y\":0.0}' rotation=\"0.0\" scaleX=\"0.5\" scaleY=\"0.5\" url=\"https://turnover-cn.oss-cn-hangzhou.aliyuncs.com/turnover/1670379863915_948.png\">\n" + "        <endAnim alpha=\"255\" displaySize=\"0\" durTime=\"1000\" interpolator=\"2\" endId=\"0\" endL='{\"x\":680.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"2.0\" scaleY=\"2.0\" url=\"\" />\n" + "        <endContainer displaySize=\"0\" durTime=\"1500\" url=\"\">\n" + "            <endAnim alpha=\"255\" displaySize=\"0\" durTime=\"1000\" interpolator=\"0\" endId=\"0\" endL='{\"x\":680.0,\"y\":0.0}' rotation=\"360.0\" scaleX=\"1.0\" scaleY=\"1.0\" url=\"\" />\n" + "            <endAnim alpha=\"0\" displaySize=\"0\" durTime=\"1000\" interpolator=\"0\" endId=\"0\" endL='{\"x\":0.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"1.0\" scaleY=\"1.0\" url=\"\" />\n" + "            <endAnim alpha=\"255\" displaySize=\"0\" durTime=\"1000\" interpolator=\"0\" endId=\"0\" endL='{\"x\":680.0,\"y\":3007.0}' rotation=\"0.0\" scaleX=\"0.0\" scaleY=\"0.0\" url=\"\" />\n" + "            <endAnim alpha=\"255\" displaySize=\"0\" durTime=\"1000\" interpolator=\"0\" endId=\"0\" endL='{\"x\":1440.0,\"y\":1463.5}' rotation=\"0.0\" scaleX=\"0.0\" scaleY=\"0.0\" url=\"\" />\n" + "        </endContainer>\n" + "    </startAnim>\n" + "</anim>"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_anim_canvas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        anim_1?.setOnClickListener {
            lifecycleScope.launch {
                repeat(100) {
                    startSingleAnim()
                    delay(50)
                }
            }
        }

        anim_2?.setOnClickListener {
            lifecycleScope.launch {
                startImageDouAnim2()
            }
        }

        anim_3?.setOnClickListener {
            startImageDouAnim3()
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

    private fun startSingleAnimOrigin() {
        val view = ImageView(context)
        relative.addView(view)
        view.visibility = View.GONE
        val size = 80
        val bitmap =
            BitmapLoader.decodeBitmapFrom(resources, R.mipmap.xin, 1, size, size)
        view.setImageBitmap(bitmap)
        val translationAnimatorSet = AnimatorSet()
        val animator = ObjectAnimator.ofFloat(
            view,
            "translationX", 0f,
            DisplayUtils.getScreenWidth(this.activity).toFloat() / 2 - size
        ).setDuration(1000)
        val animator1 = ObjectAnimator.ofFloat(
            view,
            "translationY", 0f,
            DisplayUtils.getScreenHeight(this.activity).toFloat() / 2 - size
        ).setDuration(1000)
        val animator2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f).setDuration(1000)
        val animator3 = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f).setDuration(1000)
        val animator4 = ObjectAnimator.ofFloat(
            view,
            "translationY", 0f,
        ).setDuration(1000)
        val animator5 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f).setDuration(1000)
        val animator6 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f).setDuration(1000)
        //translationAnimatorSet.playTogether(animator, animator1, animator2, animator3)
        translationAnimatorSet
            .play(animator).with(animator1).with(animator2)
            .with(animator3).before(AnimatorSet().apply {
                play(animator4).with(animator5).with(animator6)
            })
        translationAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        translationAnimatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.visibility = View.GONE
                relative.removeView(view)
            }

            override fun onAnimationCancel(animation: Animator?) {
                view.visibility = View.GONE
                relative.removeView(view)
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
        translationAnimatorSet.start()
    }

    private fun startSingleAnim() {
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
                        scaleX = 3f
                        scaleY = 3f
                        durTime = 1000
                        interpolator = InterpolatorEnum.Accelerate.type
                    }
                    endNode {
                        point = PointF(
                            DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / 2,
                            0f
                        )
                        scaleX = 0.5f
                        scaleY = 0.5f
                        durTime = 2000
                        interpolator = InterpolatorEnum.Accelerate.type
                    }
                }
            }
        }.apply {
            lifecycleScope.launch(Dispatchers.IO) {
                anim_surface ?: return@launch
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
                                    .toFloat() / 2 - size / 2, size / 2f
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
                                    .toFloat() / 2 - size / 2, size / 2f
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
                                    .toFloat() / 2 - size / 2, size / 2f
                            )
                            scaleX = 0f
                            scaleY = 0f
                            durTime = 1000
                            interpolator = InterpolatorEnum.Linear.type
                        }
                    }
                }
            }
        }.apply {
            lifecycleScope.launch(Dispatchers.IO) {
                anim_surface ?: return@launch
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


//    private fun startMoreAnim3() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            AnimDecoder.suspendPlayAnimWithXml(xmlMore, anim_surface, xmlMore) {
//                BitmapFactory.decodeResource(resources, R.mipmap.xin)
//            }
//        }
//    }

    private fun startMoreAnim2() {
        val size = 80
        val url = "https://turnover-cn.oss-cn-hangzhou.aliyuncs.com/turnover/1670379863915_948.png"
        AnimEncoder().buildAnimNode {
            imageNode {
                this.url = url
                this.displayHeightSize = size
                startNode {
                    point = PointF(0f, 0f)
                    scaleX = 0f
                    scaleY = 0f
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
                                    .toFloat() / 2 - size / 2, 0f
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
                                durTime = 1000
                                alpha = 0
                                rotation = 360f
                                scaleX = 2f
                                scaleY = 2f
                            }
                        }
                    }
                }
            }
        }.apply {
            Animer.log.i("tttt2", this.buildString())
            lifecycleScope.launch {
                anim_surface ?: return@launch
                AnimDecoder2.suspendPlayAnimWithAnimNode(
                    anim_surface, this@apply
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


    private fun startImageDouAnim2() {
        val size = 150
        val url = "http://imgs.pago.tv/gifts/69753013-38d8-44de-8a14-286cf4f81083.png"
        AnimEncoder().buildAnimNode {
            imageDouNode {
                this.rocation = 10
                this.url = url
                this.displayHeightSize = size
                startNode {
                    scaleX = 2f
                    scaleY = 2f
                    point = PointF(
                        DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                            .toFloat() / 2 - size / scaleX / 2,
                        DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                            .toFloat() - size / scaleY / 2
                    )
                    endNode {
                        scaleX = 2f
                        scaleY = 2f
                        point = PointF(
                            DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / scaleX / 2,
                            DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / scaleY / 2
                        )
                        durTime = 1000
                        interpolator = InterpolatorEnum.Decelerate.type
                    }
                    endNode {
                        scaleX = 2f
                        scaleY = 2f
                        point = PointF(
                            DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / scaleX / 2,
                            DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                .toFloat() - size / scaleY / 2
                        )
                        durTime = 2000
                        interpolator = InterpolatorEnum.Accelerate.type
                    }
                }
            }
        }.apply {
            lifecycleScope.launch {
                anim_surface ?: return@launch
                AnimDecoder2.suspendPlayAnimWithAnimNode(
                    anim_surface, this@apply
                ) { node, displayItem ->
                    when (displayItem) {
                        is BitmapDouDisplay -> {
                            loadImage(
                                fragment = this@TestAnimCanvasFragment,
                                (node as ImageNode).url,
                                node.displayHeightSize
                            )?.let {
                                displayItem.setBitmap(it)
                            }
                        }

                        is BitmapDisplayItem -> {
                            displayItem.setBitmap(
                                BitmapLoader.decodeBitmapFrom(
                                    resources, R.mipmap.xin, 1, 100, 100
                                )
                            )
                        }
                    }
                    displayItem
                }
            }
        }
    }

    private fun startImageDouAnim3() {
        AnimEncoder().buildAnimNode {
            layoutNode {
                layoutIdName = "view_liuguang"
                startNode {
                    scaleX = 1f
                    scaleY = 1f
                    point = PointF(
                        DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                            .toFloat(),
                        DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                            .toFloat() / 3
                    )
                    endNode {
                        scaleX = 1f
                        scaleY = 1f
                        point = PointF(
                            0f,
                            DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                .toFloat() / 3
                        )
                        durTime = 1500
                        interpolator = InterpolatorEnum.Decelerate.type
                    }

                    endNode {
                        scaleX = 1f
                        scaleY = 1f
                        point = PointF(
                            0f,
                            DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                .toFloat() / 3
                        )
                        durTime = 500
                        interpolator = InterpolatorEnum.Linear.type
                    }
                    endNode {
                        scaleX = 1f
                        scaleY = 1f
                        point = PointF(
                            DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                .toFloat() * -1,
                            DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                .toFloat() / 3
                        )
                        durTime = 1000
                        interpolator = InterpolatorEnum.Linear.type
                    }
                }
            }
        }.apply {
            lifecycleScope.launch {
                anim_surface ?: return@launch
                AnimDecoder2.suspendPlayAnimWithAnimNode(
                    anim_surface, this@apply
                ) { node, displayItem ->
                    when (displayItem) {
                        is LayoutDisplayItem -> {

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
            val bitmap = BitmapLoader.decodeBitmapFrom(resources, R.mipmap.xin, 1, size, size)
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
            val start = PathObject(
                displayItemId,
                point = PointF((i * 80 + (80 * (i + 1))).toFloat(), 0f),
                interpolator = LinearInterpolator(),
                scaleX = 1f,
                scaleY = 1f
            )
            val next = PathObject(
                displayItemId, point = PointF(
                    (i * 80 + (80 * (i + 1))).toFloat(),
                    DisplayUtils.getScreenHeight(this.activity).toFloat()
                ), interpolator = LinearInterpolator(), scaleX = 1f, scaleY = 1f
            )
            val time = (1000L..10000L).random()
            anim_surface?.addAnimDisplay(AnimPathObject.Inner.with().beginAnimPath(start)
                .doAnimPath(time, next).build(displayObject.build()).apply {
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
        Log.i("zzc4", "startAnim animId:$animId")
    }

    override fun runningAnim(animId: Long) {
        Log.i("zzc4", "runningAnim animId:$animId")
    }

    override fun endAnim(animId: Long) {
        Log.i("zzc4", "endAnim animId:$animId")
    }
}

suspend fun loadImage(fragment: Fragment, url: String, size: Int): Bitmap? {
    return suspendCancellableCoroutine {
        Glide.with(fragment).asBitmap()
            .load(url).into(object : CustomTarget<Bitmap>(
                size, size
            ) {
                override fun onResourceReady(
                    resource: Bitmap, transition: Transition<in Bitmap>?
                ) {
                    if (it.isActive)
                        it.resume(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    if (it.isActive)
                        it.resume(null)
                }
            })
    }
}