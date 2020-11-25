package com.lzy.nestescroll

import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.lzy.nestescroll.databinding.ActivityNestedscrollScrollviewRecyclerviewBinding
import java.util.*

class NestedViewPagerActivityTest1 : AppCompatActivity() {
    lateinit var binding: ActivityNestedscrollScrollviewRecyclerviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_nestedscroll_scrollview_recyclerview
        )
        val pagerAdapter = ViewPager2Adapter(this, pageFragments)
        binding.viewpagerView.adapter = pagerAdapter
        val labels =
            arrayOf("linear", "scroll", "recycler")
        TabLayoutMediator(
            binding.tablayout,
            binding.viewpagerView,
            TabConfigurationStrategy { tab, position -> tab.text = labels[position] }).attach()
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.getRoot().postDelayed(
                { binding.swipeRefreshLayout.isRefreshing = false }, 1000
            )
        }
        binding.swipeRefreshLayout.viewTreeObserver
            .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    val layoutParams = binding.llContent.layoutParams
                    layoutParams.height = binding.swipeRefreshLayout.measuredHeight
                    binding.llContent.layoutParams = layoutParams
                    binding.swipeRefreshLayout.viewTreeObserver.removeOnPreDrawListener(this)
                    return false
                }
            })
    }

    private val pageFragments: List<Fragment>
        get() {
            val data: MutableList<Fragment> =
                ArrayList()
            data.add(RecyclerViewFragment())
            data.add(RecyclerViewFragment())
            data.add(RecyclerViewFragment())
            return data
        }
}