package com.lzy.nestescroll.views.coordinator

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.NestedScrollType
import androidx.core.view.ViewCompat.ScrollAxis
import androidx.recyclerview.widget.RecyclerView
import com.lzy.nestescroll.R
import java.lang.reflect.Constructor

/**
 * 协调RecyclerView 内部ItemView，实现跨ItemView的协调。思路参考CoordinatorLayout<br/>
 * @author 林佐跃 <br/>
 */
class CoordinatorRecyclerView : RecyclerView, NestedScrollingParent3, NestedScrollingParent2 {

    private val mParentHelper: NestedScrollingParentHelper = NestedScrollingParentHelper(this)
    private val mBehaviorConsumed = IntArray(2)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /**
     * 用 CoordinatorRecyclerView#LayoutParams 构建ItemView的布局参数
     */
    override fun generateLayoutParams(attrs: AttributeSet?): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    /**
     * RecyclerView 是纵向滑动的，所以支持的嵌套滑动是来自横向的
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        var handled = false
        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view.visibility == View.GONE) {
                // If it's GONE, don't dispatch
                continue
            }
            if (view.layoutParams !is LayoutParams) {
                continue
            }
            val lp = view.layoutParams as LayoutParams
            val viewBehavior = lp.mBehavior
            if (viewBehavior != null) {
                val accepted = viewBehavior.onStartNestedScroll(
                    this, view,
                    target, axes, type
                )
                handled = handled or accepted
                lp.setNestedScrollAccepted(type, accepted)
            } else {
                lp.setNestedScrollAccepted(type, false)
            }
        }
        if (handled)
            return handled
        //子view如果没有要协调滑动的，那么自己处理嵌套滑动
        return when {
            axes and ViewCompat.SCROLL_AXIS_HORIZONTAL != 0 -> layoutManager?.canScrollHorizontally()
                ?: false
            axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0 -> layoutManager?.canScrollVertically()
                ?: false
            else -> false
        }
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
        var handled = false
        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view.layoutParams !is LayoutParams) {
                continue
            }
            val lp = view.layoutParams as LayoutParams
            if (!lp.isNestedScrollAccepted(type)) {
                continue
            }
            val viewBehavior = lp.mBehavior
            viewBehavior?.onNestedScrollAccepted(
                this, view, target,
                nestedScrollAxes, type
            )
            handled = true
        }

        //继续向上查找需要嵌套滑动的View
        if (!handled) {
            startNestedScroll(axes, type)
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        var xConsumed = 0
        var yConsumed = 0

        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view.visibility == View.GONE) {
                // If the child is GONE, skip...
                continue
            }
            if (view.layoutParams !is LayoutParams) {
                continue
            }
            val lp =
                view.layoutParams as LayoutParams
            if (!lp.isNestedScrollAccepted(type)) {
                continue
            }
            val viewBehavior = lp.mBehavior
            if (viewBehavior != null) {
                mBehaviorConsumed[0] = 0
                mBehaviorConsumed[1] = 0
                viewBehavior.onNestedPreScroll(this, view, target, dx, dy, mBehaviorConsumed, type)
                xConsumed =
                    if (dx > 0) xConsumed.coerceAtLeast(mBehaviorConsumed[0]) else xConsumed.coerceAtMost(
                        mBehaviorConsumed[0]
                    )
                yConsumed =
                    if (dy > 0) yConsumed.coerceAtLeast(mBehaviorConsumed[1]) else yConsumed.coerceAtMost(
                        mBehaviorConsumed[1]
                    )
            }
        }

        consumed[0] = xConsumed
        consumed[1] = yConsumed

        //按照嵌套滑动的思路，先将滑动继续向上分发,询问上层嵌套是否要消费滑动距离
        dispatchNestedPreScroll(dx, dy, consumed, null, type)
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
        val childCount = childCount
        var xConsumed = 0
        var yConsumed = 0

        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view.visibility == View.GONE) {
                // If the child is GONE, skip...
                continue
            }
            if (view.layoutParams !is LayoutParams) {
                continue
            }
            val lp =
                view.layoutParams as LayoutParams
            if (!lp.isNestedScrollAccepted(type)) {
                continue
            }
            val viewBehavior = lp.mBehavior
            if (viewBehavior != null) {
                mBehaviorConsumed[0] = 0
                mBehaviorConsumed[1] = 0
                viewBehavior.onNestedScroll(
                    this, view, target, dxConsumed, dyConsumed,
                    dxUnconsumed, dyUnconsumed, type, mBehaviorConsumed
                )
                xConsumed =
                    if (dxUnconsumed > 0) xConsumed.coerceAtLeast(mBehaviorConsumed[0]) else xConsumed.coerceAtMost(
                        mBehaviorConsumed[0]
                    )
                yConsumed =
                    if (dyUnconsumed > 0) yConsumed.coerceAtLeast(mBehaviorConsumed[1]) else yConsumed.coerceAtMost(
                        mBehaviorConsumed[1]
                    )
            }
        }

        consumed[0] += xConsumed
        consumed[1] += yConsumed

        //将没处理完的继续向上分发
        dispatchNestedScroll(
            dxConsumed + xConsumed, dyConsumed + yConsumed
            , dxUnconsumed - xConsumed, dyUnconsumed - yConsumed, null, type, consumed
        )
    }

    override fun onStopNestedScroll(target: View) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        mParentHelper.onStopNestedScroll(target, type)
        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view.layoutParams !is LayoutParams) {
                continue
            }
            val lp =
                view.layoutParams as LayoutParams
            if (!lp.isNestedScrollAccepted(type)) {
                continue
            }
            lp.mBehavior?.onStopNestedScroll(this, view, target, type)
            lp.resetNestedScroll(type)
        }
        stopNestedScroll(type)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        var handled = false
        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view.visibility == View.GONE) {
                // If the child is GONE, skip...
                continue
            }
            if (view.layoutParams !is LayoutParams) {
                continue
            }
            val lp =
                view.layoutParams as LayoutParams
            if (!lp.isNestedScrollAccepted(ViewCompat.TYPE_TOUCH)) {
                continue
            }
            val viewBehavior = lp.mBehavior
            if (viewBehavior != null) {
                handled = handled or viewBehavior.onNestedPreFling(
                    this,
                    view,
                    target,
                    velocityX,
                    velocityY
                )
            }
        }
        if (handled)
            return true
        return dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        var handled = false

        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view.visibility == View.GONE) {
                // If the child is GONE, skip...
                continue
            }
            if (view.layoutParams !is LayoutParams) {
                continue
            }
            val lp =
                view.layoutParams as LayoutParams
            if (!lp.isNestedScrollAccepted(ViewCompat.TYPE_TOUCH)) {
                continue
            }
            val viewBehavior = lp.mBehavior
            if (viewBehavior != null) {
                handled = handled or viewBehavior.onNestedFling(
                    this, view, target, velocityX, velocityY,
                    consumed
                )
            }
        }
        //如果协调处理了直接返回
        if (handled) {
            return true
        }

        //如果内部的RecyclerView 消费了则自己不消费，否则处理后续fling
        dispatchNestedFling(velocityX, velocityY, true)
        fling(velocityX.toInt(), velocityY.toInt())

        return true
    }


    class LayoutParams @SuppressLint("CustomViewStyleable") constructor(
        c: Context,
        attrs: AttributeSet?
    ) : RecyclerView.LayoutParams(c, attrs) {

        companion object {

            private val sConstructors = ThreadLocal<Map<String, Constructor<*>>>()

            fun parseBehavior(
                context: Context,
                attrs: AttributeSet?,
                name: String
            ): Behavior? {
                if (TextUtils.isEmpty(name)) {
                    return null
                }
                val fullName: String = if (name.startsWith(".")) {
                    // Relative to the app package. Prepend the app package name.
                    context.packageName + name
                } else if (name.indexOf('.') >= 0) {
                    // Fully qualified package name.
                    name
                } else {
                    ""
                }
                if (fullName.isEmpty()) {
                    return null
                }
                try {
                    var constructors =
                        sConstructors.get()
                    if (constructors == null) {
                        constructors =
                            HashMap()
                        sConstructors.set(constructors)
                    }
                    var c = constructors[fullName]
                    if (c == null) {
                        val clazz = Class.forName(
                            fullName,
                            false,
                            context.classLoader
                        )

                        c = clazz.getDeclaredConstructor(
                            Context::class.java,
                            AttributeSet::class.java
                        )
                        c.isAccessible = true
                        (constructors as HashMap<String, Constructor<*>>)[fullName] = c
                    }
                    return c.newInstance(context, attrs) as Behavior?
                } catch (e: Exception) {
                    throw RuntimeException(
                        "Could not inflate Behavior subclass $fullName",
                        e
                    )
                }
                return null
            }


        }

        var mBehaviorResolved = false
        var mBehavior: Behavior? = null
            set(value) {
                if (field !== value) {
                    field?.onDetachedFromLayoutParams()
                    field = value
                    mBehaviorResolved = true
                    mBehavior?.onAttachedToLayoutParams(this)
                }
            }
        private var mDidAcceptNestedScrollTouch = false
        private var mDidAcceptNestedScrollNonTouch = false

        @IdRes
        var id = -1


        init {
            val a =
                c.obtainStyledAttributes(attrs, R.styleable.CoordinatorRecyclerView_LayoutParams)

            id =
                a.getResourceId(R.styleable.CoordinatorRecyclerView_LayoutParams_coordinator_id, -1)

            mBehaviorResolved =
                a.hasValue(R.styleable.CoordinatorRecyclerView_LayoutParams_coordinator_behavior)
            if (mBehaviorResolved) {
                mBehavior = parseBehavior(
                    c,
                    attrs,
                    a.getString(R.styleable.CoordinatorRecyclerView_LayoutParams_coordinator_behavior)!!
                )
            }
            a.recycle()

            mBehavior?.onAttachedToLayoutParams(this)

        }

        fun resetNestedScroll(type: Int) {
            setNestedScrollAccepted(type, false)
        }

        fun setNestedScrollAccepted(type: Int, accept: Boolean) {
            when (type) {
                ViewCompat.TYPE_TOUCH -> mDidAcceptNestedScrollTouch = accept
                ViewCompat.TYPE_NON_TOUCH -> mDidAcceptNestedScrollNonTouch = accept
            }
        }

        fun isNestedScrollAccepted(type: Int): Boolean {
            when (type) {
                ViewCompat.TYPE_TOUCH -> return mDidAcceptNestedScrollTouch
                ViewCompat.TYPE_NON_TOUCH -> return mDidAcceptNestedScrollNonTouch
            }
            return false
        }


    }

    open class Behavior {

        var params: LayoutParams? = null

        constructor()

        constructor(context: Context, attrs: AttributeSet) : this()

        fun onAttachedToLayoutParams(params: LayoutParams) {
            this.params = params
        }

        fun onDetachedFromLayoutParams() {
            params = null
        }

        open fun onStartNestedScroll(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View,
            target: View,
            @ScrollAxis axes: Int
        ): Boolean {
            return false
        }

        fun onStartNestedScroll(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View, target: View,
            @ScrollAxis axes: Int, @NestedScrollType type: Int
        ): Boolean {
            return if (type == ViewCompat.TYPE_TOUCH) {
                onStartNestedScroll(
                    coordinatorRecyclerView, child,
                    target, axes
                )
            } else false
        }

        open fun onNestedScrollAccepted(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View,
            target: View,
            @ScrollAxis axes: Int
        ) {
            // Do nothing
        }

        fun onNestedScrollAccepted(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View, target: View,
            @ScrollAxis axes: Int, @NestedScrollType type: Int
        ) {
            if (type == ViewCompat.TYPE_TOUCH) {
                onNestedScrollAccepted(
                    coordinatorRecyclerView, child,
                    target, axes
                )
            }
        }

        open fun onStopNestedScroll(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View,
            target: View
        ) {
            // Do nothing
        }

        fun onStopNestedScroll(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View, target: View, @NestedScrollType type: Int
        ) {
            if (type == ViewCompat.TYPE_TOUCH) {
                onStopNestedScroll(coordinatorRecyclerView, child, target)
            }
        }

        open fun onNestedScroll(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View,
            target: View,
            dxConsumed: Int,
            dyConsumed: Int,
            dxUnconsumed: Int,
            dyUnconsumed: Int
        ) {
            // Do nothing
        }

        fun onNestedScroll(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View,
            target: View,
            dxConsumed: Int,
            dyConsumed: Int,
            dxUnconsumed: Int,
            dyUnconsumed: Int,
            @NestedScrollType type: Int
        ) {
            if (type == ViewCompat.TYPE_TOUCH) {
                onNestedScroll(
                    coordinatorRecyclerView, child, target, dxConsumed, dyConsumed,
                    dxUnconsumed, dyUnconsumed
                )
            }
        }

        fun onNestedScroll(
            coordinatorRecyclerView: CoordinatorRecyclerView, child: View,
            target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
            dyUnconsumed: Int, @NestedScrollType type: Int, consumed: IntArray
        ) {
            // In the case that this nested scrolling v3 version is not implemented, we call the v2
            // version in case the v2 version is. We Also consume all of the unconsumed scroll
            // distances.
            consumed[0] += dxUnconsumed
            consumed[1] += dyUnconsumed
            onNestedScroll(
                coordinatorRecyclerView, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, type
            )
        }

        open fun onNestedPreScroll(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View,
            target: View,
            dx: Int,
            dy: Int,
            consumed: IntArray
        ) {
            // Do nothing
        }

        fun onNestedPreScroll(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View, target: View, dx: Int, dy: Int, consumed: IntArray,
            @NestedScrollType type: Int
        ) {
            if (type == ViewCompat.TYPE_TOUCH) {
                onNestedPreScroll(coordinatorRecyclerView, child, target, dx, dy, consumed)
            }
        }

        open fun onNestedFling(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View, target: View, velocityX: Float, velocityY: Float,
            consumed: Boolean
        ): Boolean {
            return false
        }

        open fun onNestedPreFling(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View, target: View, velocityX: Float, velocityY: Float
        ): Boolean {
            return false
        }
    }

}