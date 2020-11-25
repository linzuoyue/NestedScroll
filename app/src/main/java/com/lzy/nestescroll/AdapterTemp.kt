package com.lzy.nestescroll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import java.util.*

/**
 * @author 林佐跃 <br></br>
 */
class AdapterTemp(private val nestedViewPagerActivityTest2: NestedViewPagerActivityTest2) :
    RecyclerView.Adapter<AdapterTemp.BaseViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        return if (viewType != itemCount - 1) {
            BaseViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recycler1, parent, false)
            )
        } else BaseViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recycler2, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int
    ) {
        if (position == itemCount - 1) {
            val viewpagerView: ViewPager2 = holder.itemView.findViewById(R.id.viewpager_view)
            if (viewpagerView.adapter != null) {
                return
            }
            val pagerAdapter =
                ViewPager2Adapter(nestedViewPagerActivityTest2, pageFragments)
            viewpagerView.adapter = pagerAdapter
            viewpagerView.isNestedScrollingEnabled = false
            val labels =
                arrayOf("linear", "scroll", "recycler")
            val tabLayout: TabLayout = holder.itemView.findViewById(R.id.tablayout)
            TabLayoutMediator(
                tabLayout,
                viewpagerView,
                TabConfigurationStrategy { tab, position -> tab.text = labels[position] }).attach()
        } else {
            (holder.itemView as TextView).text = "head$position"
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return 5
    }

    class BaseViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    private val pageFragments: List<Fragment>
        private get() {
            val data: MutableList<Fragment> =
                ArrayList()
            data.add(RecyclerViewFragment())
            data.add(RecyclerViewFragment())
            data.add(RecyclerViewFragment())
            return data
        }

}