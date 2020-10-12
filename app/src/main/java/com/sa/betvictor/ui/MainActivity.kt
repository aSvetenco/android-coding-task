package com.sa.betvictor.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sa.betvictor.R

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(
                    R.id.frameLayout,
                    TweetListFragment.newInstance(),
                    TweetListFragment::class.java.simpleName
                )
            }.commit()
        }
    }
}