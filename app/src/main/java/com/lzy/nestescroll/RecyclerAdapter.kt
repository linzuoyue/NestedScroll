package com.lzy.nestescroll

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lzy.nestescroll.views.BaseViewHolder
import com.lzy.nestescroll.views.titleview.TitleView
import com.lzy.nestescroll.views.titleview.TitleViewViewModel

class RecyclerAdapter(var data: ArrayList<String>) :
    RecyclerView.Adapter<BaseViewHolder<TitleViewViewModel>>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<TitleViewViewModel> {
        return BaseViewHolder(TitleView(parent.context))
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<TitleViewViewModel>,
        position: Int
    ) {
        holder.bind(TitleViewViewModel(data[position]))
    }

    override fun getItemCount(): Int {
        return data.size
    }

}