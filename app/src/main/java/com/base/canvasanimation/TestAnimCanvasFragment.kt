package com.base.canvasanimation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
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
import com.base.animation.xml.AnimEncoder
import com.base.animation.xml.buildAnimNode
import com.base.animation.xml.buildString
import com.base.animation.xml.node.coder.InterpolatorEnum
import com.base.animation.xml.node.coder.Location
import com.base.animation.xml.node.coder.ValueLoader
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


/**
 * @author:zhouzechao
 * @date: 2020/11/17
 * description：
 */
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class TestAnimCanvasFragment : Fragment(), IClickIntercept, IAnimListener {

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
                repeat(200) {
                    startSingleAnimOrigin()
                    delay(50)
                }
            }

        }

        anim_2?.setOnClickListener {
            lifecycleScope.launch {
                repeat(200) {
                    startMoreAnim2()
                    delay(50)
                }
            }
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

    /**
     * 单个动画
     */
    private fun startSingleAnim() {
        val size = 80

        val displayObject = DisplayObject.with(animView = anim_surface)
        val displayItemId1 = displayObject.add(
            key = "xin_startSingleAnim", kClass = BitmapDisplayItem::class, roomView = anim_surface
        ) {
            val bitmap =
                BitmapLoader.decodeBitmapFrom(resources, R.mipmap.xin, 1, size, size)
            val bitmapWidth = bitmap.width
            val bitmapHeight = bitmap.height
            val displayWidth = size * bitmapWidth / bitmapHeight
            return@add BitmapDisplayItem.of(bitmap).apply {
                setDisplaySize(displayWidth, size)
            }
        }

        val start = PathObject(
            displayItemId1,
            point = PointF(0f, 0f),
            interpolator = DecelerateInterpolator(),
            scaleX = 0.5f,
            scaleY = 0.5f
        )
        val next = PathObject(
            displayItemId1, point = PointF(
                DisplayUtils.getScreenWidth(this.activity).toFloat() / 2 - size / 2,
                DisplayUtils.getScreenHeight(this.activity).toFloat() / 2 - size / 2
            ), interpolator = AccelerateDecelerateInterpolator(), scaleX = 2.0f,
            scaleY = 2.0f
        )
        val next1 = PathObject(
            displayItemId1,
            point = PointF(DisplayUtils.getScreenWidth(this.activity).toFloat() / 2 - size / 2, 9f),
            scaleX = 0.5f,
            scaleY = 0.5f
        )
        val next2 = PathObject(
            displayItemId1,
            point = PointF(DisplayUtils.getScreenWidth(this.activity).toFloat() / 2 - size / 2, 9f),
            scaleX = 1.0f,
            scaleY = 1.0f
        )
        val next3 = PathObject(
            displayItemId1,
            point = PointF(DisplayUtils.getScreenWidth(this.activity).toFloat() / 2 - size / 2, 9f),
            scaleX = 0.5f,
            scaleY = 0.5f
        )
        anim_surface?.addAnimDisplay(
            AnimPathObject.Inner.with(
                displayObject.build(), true
            ).beginAnimPath(start).doAnimPath(1000, next).beginNextAnimPath(next)
                .doAnimPath(1000, next1).beginNextAnimPath(next1).doAnimPath(200, next2)
                .beginNextAnimPath(next2)
                .doAnimPath(200, next3).build()
        )
    }

    private fun startSingleAnim2() {
        val size = 80
        val url = "https://turnover-cn.oss-cn-hangzhou.aliyuncs.com/turnover/1670379863915_948.png"
        AnimEncoder().buildAnimNode {
            startNode {
                this.url = url
                point = ValueLoader.toJsonString(Location(0f, 0f))
                scaleX = 0.5f
                scaleY = 0.5f
                displayHeightSize = 80
                endNode {
                    point = ValueLoader.toJsonString(
                        Location(
                            DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / 2,
                            DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / 2
                        )
                    )
                    scaleX = 2f
                    scaleY = 2f
                    durTime = 1000
                    interpolator = InterpolatorEnum.Accelerate.type
                }
                endNode {
                    point = ValueLoader.toJsonString(
                        Location(
                            DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / 2, 9f
                        )
                    )
                    scaleX = 0.5f
                    scaleY = 0.5f
                    durTime = 1000
                }
            }
        }.apply {
            Log.i("tttt2", this.buildString())
            lifecycleScope.launch(Dispatchers.IO) {
                AnimDecoder.suspendPlayAnimWithNode(url, anim_surface, this@apply) {
                    BitmapFactory.decodeResource(resources, R.mipmap.xin)
                }
            }
        }
    }

    /**
     * 多个动画
     */
    private fun startMoreAnim() {
        val size = 80

        val displayObject = DisplayObject.with(animView = anim_surface)
        val displayItemId = displayObject.add(
            key = "xin_startMoreAnim", kClass = BitmapDisplayItem::class, roomView = anim_surface
        ) {
            val bitmap =
                BitmapLoader.decodeBitmapFrom(resources, R.mipmap.xin, 1, size, size)
            val bitmapWidth = bitmap.width
            val bitmapHeight = bitmap.height
            val displayWidth = size * bitmapWidth / bitmapHeight
            return@add BitmapDisplayItem.of(bitmap).apply {
                setDisplaySize(displayWidth, size)
            }
        }

        val start = PathObject(
            displayItemId,
            point = PointF(0f, 0f),
            interpolator = DecelerateInterpolator()
        )
        val next = PathObject(
            displayItemId, point = PointF(
                DisplayUtils.getScreenWidth(this.activity).toFloat() / 2 - size / 2,
                DisplayUtils.getScreenHeight(this.activity).toFloat() / 2 - size / 2
            ), interpolator = AccelerateDecelerateInterpolator()
        )
        val next1 =
            PathObject(
                displayItemId,
                point = PointF(
                    DisplayUtils.getScreenWidth(this.activity).toFloat() / 2 - size / 2,
                    0f
                ),
                rotation = 360f
            )
        val next2 = PathObject(
            displayItemId,
            point = PointF(
                0f,
                DisplayUtils.getScreenHeight(this.activity).toFloat() / 2 - size / 2
            ),
            alpha = 0
        )
        val next3 = PathObject(
            displayItemId, point = PointF(
                DisplayUtils.getScreenWidth(this.activity).toFloat() / 2 - size / 2,
                DisplayUtils.getScreenHeight(this.activity).toFloat()
            ), scaleX = 0f, scaleY = 0f
        )
        val next4 = PathObject(
            displayItemId, point = PointF(
                DisplayUtils.getScreenWidth(this.activity).toFloat(),
                DisplayUtils.getScreenHeight(this.activity).toFloat() / 2 - size / 2
            )
        )
        anim_surface?.addAnimDisplay(
            AnimPathObject.Inner.with(displayObject.build(), true).beginAnimPath(start)
                .doAnimPath(1500, next)
                .beginNextAnimPath(next)
                .doAnimPaths(1500, mutableListOf(next1, next2, next3, next4)).build()
        )
    }

    private fun startMoreAnim2() {
        val size = 80
        val url = "https://turnover-cn.oss-cn-hangzhou.aliyuncs.com/turnover/1670379863915_948.png"
        AnimEncoder().buildAnimNode {
            startNode {
                this.url = url
                point = ValueLoader.toJsonString(Location(0f, 0f))
                scaleX = 0.5f
                scaleY = 0.5f
                displayHeightSize = 80
                endNode {
                    point = ValueLoader.toJsonString(
                        Location(
                            DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / 2,
                            DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                .toFloat() / 2 - size / 2
                        )
                    )
                    scaleX = 2f
                    scaleY = 2f
                    durTime = 1000
                    interpolator = InterpolatorEnum.Decelerate.type
                }
                endContainer {
                    durTime = 1500
                    endNode {
                        rotation = 360f
                        point = ValueLoader.toJsonString(
                            Location(
                                DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                    .toFloat() / 2 - size / 2,
                                0f
                            )
                        )
                    }
                    endNode {
                        point = ValueLoader.toJsonString(
                            Location(
                                0f,
                                DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                    .toFloat() / 2 - size / 2
                            )
                        )
                        alpha = 0
                    }
                    endNode {
                        point = ValueLoader.toJsonString(
                            Location(
                                DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                    .toFloat() / 2 - size / 2,
                                DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                    .toFloat()
                            )
                        )
                        scaleX = 0f
                        scaleY = 0f
                    }
                    endNode {
                        point = ValueLoader.toJsonString(
                            Location(
                                DisplayUtils.getScreenWidth(this@TestAnimCanvasFragment.context)
                                    .toFloat(),
                                DisplayUtils.getScreenHeight(this@TestAnimCanvasFragment.context)
                                    .toFloat() / 2 - size / 2
                            )
                        )
                        scaleX = 0f
                        scaleY = 0f
                    }
                }
            }
        }.apply {
            lifecycleScope.launch {
                AnimDecoder.suspendPlayAnimWithNode(url, anim_surface, this@apply) {
                    BitmapFactory.decodeResource(resources, R.mipmap.xin)
                }
            }
        }
    }

    /**
     * 动画雨
     */
    private fun startAnimRain() {
        val size = 80
        val displayObject = DisplayObject.with(animView = anim_surface)
        val displayItemId = displayObject.add(
            key = "xin_startMoreAnim", kClass = BitmapDisplayItem::class, roomView = anim_surface
        ) {
            val bitmap =
                BitmapLoader.decodeBitmapFrom(resources, R.mipmap.xin, 1, size, size)
            val bitmapWidth = bitmap.width
            val bitmapHeight = bitmap.height
            val displayWidth = size * bitmapWidth / bitmapHeight
            return@add BitmapDisplayItem.of(bitmap).apply {
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
                AnimPathObject.Inner.with(displayObject.build()).beginAnimPath(start)
                    .doAnimPath(time, next).build()
                    .apply {
                        clickable = true
                        expand = "rain"
                    })
        }
    }

    override fun intercept(animId: Long, animDrawObject: AnimDrawObject, touchPointF: PointF) {
        Toast.makeText(this@TestAnimCanvasFragment.context, "$animId", Toast.LENGTH_SHORT).show()
        anim_surface?.removeAnimId(animId)
        startSingleAnim()
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