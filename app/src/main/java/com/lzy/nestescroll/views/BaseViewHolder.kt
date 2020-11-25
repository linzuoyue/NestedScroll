package com.lzy.nestescroll.views

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lzy.nestescroll.views.customview.BaseCustomViewModel
import com.lzy.nestescroll.views.customview.ICustomView

class BaseViewHolder<S : BaseCustomViewModel>(var view: ICustomView<S>) :
    RecyclerView.ViewHolder((view as View)) {
    fun bind(item: S) {
        view.setData(item)
    }

}