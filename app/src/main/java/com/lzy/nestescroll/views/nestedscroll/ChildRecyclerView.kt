package com.lzy.nestescroll.views.nestedscroll

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * 嵌套滑动内部RecyclerView <br/>
 * @author 林佐跃 <br/>
 */
class ChildRecyclerView : RecyclerView {

    private var downX //按下时 的X坐标
            = 0f
    private var downY //按下时 的Y坐标
            = 0f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.x
        val y = ev.y
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                //将按下时的坐标存储
                downX = x
                downY = y
                //按下事件禁止parent View对后续事件的拦截
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx: Float = x - downX
                val dy: Float = y - downY
                //通过距离差判断方向
                when (getOrientation(dx, dy)) {
                    //1.如果是横向滑动则允许拦截;
                    //2.如果 ChildRecyclerView 在ViewPager中 那么ViewPager就会拦截后续的事件，后续事件不会再分发到ChildRecyclerView
                    ORIENTATION_R, ORIENTATION_L -> parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    private fun getOrientation(dx: Float, dy: Float): Int {
        return if (abs(dx) > abs(dy)) {
            //X轴移动
            if (dx > 0) ORIENTATION_R else ORIENTATION_L //右,左
        } else {
            //Y轴移动
            if (dy > 0) ORIENTATION_D else ORIENTATION_U //下//上
        }
    }


    companion object {

        const val ORIENTATION_R: Int = 'r'.toInt()
        const val ORIENTATION_L: Int = 'l'.toInt()
        const val ORIENTATION_U: Int = 'u'.toInt()
        const val ORIENTATION_D: Int = 'd'.toInt()

    }


}