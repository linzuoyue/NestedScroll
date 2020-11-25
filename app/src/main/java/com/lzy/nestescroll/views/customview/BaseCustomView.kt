package com.lzy.nestescroll.views.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseCustomView<T : ViewDataBinding?, S : BaseCustomViewModel> : LinearLayout,
    ICustomView<S>, View.OnClickListener {
    protected var dataBinding: T? = null
        private set
    private var viewModel: S? = null
    private val mListener: ICustomViewActionListener? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    override fun getRootView(): View {
        return dataBinding!!.root
    }

    private fun init() {
        val inflater = this.context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (setViewLayoutId() != 0) {
            dataBinding = DataBindingUtil.inflate<T>(inflater, setViewLayoutId(), this, false)
            dataBinding?.root?.let {
                it.setOnClickListener { view ->
                    mListener?.onAction(
                        ICustomViewActionListener.ACTION_ROOT_VIEW_CLICKED,
                        view,
                        viewModel
                    )
                    onRootClick(view)
                }
                addView(it)
            }
        }
    }

    override fun setData(data: S) {
        viewModel = data
        setDataToView(viewModel)
        dataBinding?.executePendingBindings()
        onDataUpdated()
    }

    private fun onDataUpdated() {}
    override fun onClick(v: View) {}

    protected abstract fun setViewLayoutId(): Int
    protected abstract fun setDataToView(data: S?)
    protected abstract fun onRootClick(view: View?)
}