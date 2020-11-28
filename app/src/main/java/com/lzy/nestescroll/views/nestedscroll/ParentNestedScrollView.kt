package com.lzy.nestescroll.views.nestedscroll

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
        onNestedPreScroll(target, dx, dy, consumed, TYPE_TOUCH)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        //按照嵌套滑动的思路，先将滑动继续向上分发
        super.onNestedPreScroll(target, dx, dy, consumed, TYPE_TOUCH)
        //当还有滑动距离则自己尝试滑动， 只有向上滑才需要处理
        if (dy - consumed[1] > 0) {
            //dy - consumed[1] > 0 表示向上滑
            onNestedScroll(target, dx, dy, dx - consumed[0], dy - consumed[1], TYPE_TOUCH, consumed)
        }
    }

}