package com.lzy.nestescroll.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat.TYPE_TOUCH
import androidx.core.widget.NestedScrollView

/**
 * 嵌套滑动父类 <br/>
 * @author 林佐跃 <br/>
 */
class ParentNestedScrollView : NestedScrollView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        dispatchNestedPreScroll(dx, dy, consumed, null, TYPE_TOUCH)
        if (dy - consumed[1] > 0) {
            onNestedScroll(target, dx, dy, dx - consumed[0], dy - consumed[1], TYPE_TOUCH, consumed)
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        dispatchNestedPreScroll(dx, dy, consumed, null, TYPE_TOUCH)
        if (dy - consumed[1] > 0) {
            onNestedScroll(target, dx, dy, dx - consumed[0], dy - consumed[1], TYPE_TOUCH, consumed)
        }
    }

}