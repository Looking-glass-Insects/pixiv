package com.eps3rd.pixiv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eps3rd.app.R
import com.eps3rd.pixiv.fragment.BlankFragment

class ScanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = BlankFragment()
        fragmentTransaction.add(R.id.container, fragment)
        fragmentTransaction.commit()
    }
}