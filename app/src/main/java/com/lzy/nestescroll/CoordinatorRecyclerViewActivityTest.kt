package com.lzy.nestescroll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_coordinator_recycler_view.*

class CoordinatorRecyclerViewActivityTest : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinator_recycler_view)
        c_rv.layoutManager = LinearLayoutManager(this)
        c_rv.adapter = CoordinatorAdapter()
    }


}