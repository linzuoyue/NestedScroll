package com.lzy.nestescroll

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.lzy.nestescroll.databinding.ActivityNestedscrollRecyclerviewBinding

class NestedViewPagerActivityTest2 : AppCompatActivity() {
    lateinit var binding: ActivityNestedscrollRecyclerviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nestedscroll_recyclerview)
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.root.postDelayed(
                { binding.swipeRefreshLayout.isRefreshing = false }, 1000
            )
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = AdapterTemp(this)
    }

    override fun onResume() {
        super.onResume()
        val height = binding.swipeRefreshLayout.height
        val measuredHeight = binding.swipeRefreshLayout.measuredHeight
        Log.e("lzy", "height:$height   measuredHeight:$measuredHeight")
    }
}