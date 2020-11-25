package com.lzy.nestescroll.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * 协调内部 滑动的RecyclerView<br/>
 * @author 林佐跃 <br/>
 */
class CoordinatorRecyclerView : RecyclerView, NestedScrollingParent3, NestedScrollingParent2 {


    private val mParentHelper: NestedScrollingParentHelper = NestedScrollingParentHelper(this)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    /**
     * 当滑动方向是纵向的时候，告诉子View 我支持嵌套滑动
     * ps：这里大家可以扩展支持横向
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean =
        when {
            axes and ViewCompat.SCROLL_AXIS_HORIZONTAL != 0 -> !(layoutManager?.canScrollHorizontally()
                ?: false)
            else -> false
        }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH)
    }

    /**
     * onStartNestedScroll 放回true 的时候 会调用该方法
     */
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        //保存处理的事件状态
        mParentHelper.onNestedScrollAccepted(child, target, axes, type)
        //继续向上查找需要嵌套滑动的View
        startNestedScroll(axes, type)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH)
    }


    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        //按照嵌套滑动的思路，先将滑动继续向上分发,询问上层嵌套是否要消费滑动距离
        dispatchNestedPreScroll(dx, dy, consumed, null, type)
        //如果自己能滑动
        if (layoutManager?.canScrollVertically() == true) {
            if (dy - consumed[1] > 0) //如果是向上滑动 自己要优先内部的RecyclerView 滑动
                doNestedScroll(dy - consumed[1], IntArray(2), consumed)
        }
    }


    override fun getNestedScrollAxes(): Int {
        return mParentHelper.nestedScrollAxes
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        onNestedScroll(
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            ViewCompat.TYPE_TOUCH
        )
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        onNestedScroll(
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            ViewCompat.TYPE_TOUCH,
            IntArray(2)
        )
    }

    /**
     * 这里会执行内部滑动之后剩余未消耗的滑动
     */
    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        val unconsumedY: Int
        val consumedY: Int
        val tempConsumed = IntArray(2)
        //先自己处理滑动
        doNestedScroll(dyUnconsumed, tempConsumed, consumed)
        consumedY = tempConsumed[1] + dyConsumed
        unconsumedY = dyUnconsumed - tempConsumed[1]
        //将没处理完的继续向上分发
        dispatchNestedScroll(dxConsumed, consumedY, dxUnconsumed, unconsumedY, null, type, consumed)
    }

    /**
     * 自己处理滑动
     */
    private fun doNestedScroll(dyUnconsumed: Int, tempConsumed: IntArray, consumed: IntArray) {
        if (adapter != null) {
            if (dyUnconsumed != 0) {
//                tempConsumed[1] =
//                    layoutManager?.scrollVerticallyBy(dyUnconsumed, getMRecycler(), getMState())
//                        ?: 0
            }
        }
        if (itemDecorationCount > 0) {
            invalidate()
        }
        consumed[0] += tempConsumed[0]
        consumed[1] += tempConsumed[1]
    }


    override fun onStopNestedScroll(target: View) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        mParentHelper.onStopNestedScroll(target, type)
        stopNestedScroll(type)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        //如果内部的RecyclerView 消费了则自己不消费，否则处理后续fling
        if (!consumed) {
            dispatchNestedFling(0f, velocityY, true)
            fling(0, velocityY.toInt())
            return true
        }
        return false
    }


}