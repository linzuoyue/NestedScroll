package com.lzy.nestescroll;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lzy.nestescroll.databinding.ActivityNestedscrollRecyclerviewBinding;


public class NestedViewPagerActivityTest2 extends AppCompatActivity {
    ActivityNestedscrollRecyclerviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nestedscroll_recyclerview);
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.getRoot().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(new AdapterTemp(this));
    }



    @Override
    protected void onResume() {
        super.onResume();
        int height = binding.swipeRefreshLayout.getHeight();
        int measuredHeight = binding.swipeRefreshLayout.getMeasuredHeight();
        Log.e("lzy", "height:" + height + "   measuredHeight:" + measuredHeight);
    }
}
