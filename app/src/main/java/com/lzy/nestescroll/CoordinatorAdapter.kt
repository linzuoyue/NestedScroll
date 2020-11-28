package com.lzy.nestescroll

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lzy.nestescroll.views.coordinator.CoordinatorRecyclerView
import kotlinx.android.synthetic.main.item_coordinator.view.*
import java.util.*

/**
 * @author 林佐跃 <br/>
 */
class CoordinatorAdapter : RecyclerView.Adapter<CoordinatorAdapter.BaseViewHolder>() {

    var mScrollX = 0

    private val mData: ArrayList<String>
        get() {
            val data: ArrayList<String> =
                ArrayList()
            var i = 0
            while (i < 10) {
                data.add("item $i")
                i++
            }
            return data
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coordinator, parent, false)
        inflate.rv.layoutManager =
            LinearLayoutManager(parent.context, RecyclerView.HORIZONTAL, false)
        inflate.rv.adapter = RecyclerAdapter(mData)
        return BaseViewHolder(inflate)
    }

    override fun getItemCount(): Int = 20

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.itemView.item_file_name.text = position.toString()
        val layoutParams = holder.itemView.layoutParams
        if (layoutParams is CoordinatorRecyclerView.LayoutParams) {
            holder.itemView.scrollTo(mScrollX, 0)
        }
    }


    class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    private class TestBehavior(context: Context, attrs: AttributeSet) :
        CoordinatorRecyclerView.Behavior(context, attrs) {

        override fun onStartNestedScroll(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View,
            target: View,
            axes: Int
        ): Boolean {
            params?.apply {
                val coordinatorView = child.findViewById<View>(id)
                if (coordinatorView is RecyclerView && coordinatorView != target) {
                    //在开始协调滑动之前，先停止之前的滑动
                    coordinatorView.stopScroll()
                    return true
                }
            }
            return false
        }

        override fun onNestedScrollAccepted(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View,
            target: View,
            axes: Int
        ) {
            params?.apply {
                val coordinatorView = child.findViewById<View>(id)
                if (coordinatorView is RecyclerView && coordinatorView != target) {
                    coordinatorView.onNestedScrollAccepted(coordinatorView, target, axes)
                }
            }
        }

        override fun onStopNestedScroll(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View,
            target: View
        ) {
            params?.apply {
                val coordinatorView = child.findViewById<View>(id)
                if (coordinatorView is RecyclerView && coordinatorView != target) {
                    coordinatorView.onStopNestedScroll(target)
                }
            }
        }

        override fun onNestedScroll(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View,
            target: View,
            dxConsumed: Int,
            dyConsumed: Int,
            dxUnconsumed: Int,
            dyUnconsumed: Int
        ) {
            params?.apply {
                val coordinatorView = child.findViewById<View>(id)
                if (coordinatorView is RecyclerView && coordinatorView != target) {
                    coordinatorView.scrollBy(dxConsumed, dyConsumed)
                }
            }
        }

        override fun onNestedFling(
            coordinatorRecyclerView: CoordinatorRecyclerView,
            child: View,
            target: View,
            velocityX: Float,
            velocityY: Float,
            consumed: Boolean
        ): Boolean {
            if (consumed) {
                params?.apply {
                    val coordinatorView = child.findViewById<View>(id)
                    if (coordinatorView is RecyclerView && coordinatorView != target) {
                        return coordinatorView.fling(velocityX.toInt(), velocityY.toInt())
                    }
                }
            }
            return false
        }

    }

}