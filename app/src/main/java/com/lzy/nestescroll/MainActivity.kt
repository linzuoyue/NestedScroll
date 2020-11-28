package com.lzy.nestescroll

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onButtonClick(v: View) {

        when (v.id) {
            R.id.nested_scroll_test1 -> {
                val intent = Intent(this, NestedViewPagerActivityTest1::class.java)
                startActivity(intent)
            }
            R.id.nested_scroll_test2 -> {
                val intent = Intent(this, NestedViewPagerActivityTest2::class.java)
                startActivity(intent)
            }
            R.id.nested_scroll_test3 -> {
                val intent = Intent(this, CoordinatorRecyclerViewActivityTest::class.java)
                startActivity(intent)
            }
        }


    }
}