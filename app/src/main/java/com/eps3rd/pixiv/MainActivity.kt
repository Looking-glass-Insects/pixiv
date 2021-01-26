package com.eps3rd.pixiv

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.launcher.ARouter
import com.eps3rd.baselibrary.Constants
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mDrawerHeader: ViewGroup
    private lateinit var mList: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDrawerHeader = findViewById(R.id.drawer_header)
        mList = findViewById(R.id.drawer_list)
        setupStatusBar()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

        setupDrawer()


        val tabFragmentList: MutableList<Fragment> = ArrayList()
        try {
            val param = Bundle()
            param.putString("param1", "t1")
            param.putString("param2", "t2")
            val fragment: Fragment =
                ARouter.getInstance().build(Constants.FRAGMENT_PATH_BLANK)
                    .with(param)
                    .navigation() as Fragment
            for (i in 0 until 1) {
                tabFragmentList.add(fragment)
            }
        } catch (e: Exception) {
            Log.d(TAG, "error to add fragments")
        }


        viewPager.adapter = object : FragmentStateAdapter(
            supportFragmentManager,
            lifecycle
        ) {
            override fun getItemCount(): Int {
                return tabFragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return tabFragmentList[position]
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "OBJECT ${(position + 1)}"
        }.attach()
    }

    private fun setupStatusBar() {
//        window.statusBarColor = Color.TRANSPARENT
    }

    private fun setupDrawer() {
        val adapter = ExpandListAdapter()

        adapter.addItem(object : ExpandListAdapter.ItemStruct {
            override fun getTitle(): String {
                return "title"
            }

            override fun getExpandView(): View? {
                val subTv = TextView(this@MainActivity)
                subTv.text = "test subTv"
                return subTv
            }

            override fun getSwitchListener(): CompoundButton.OnCheckedChangeListener? {
                return CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    Log.d(TAG,"isChecked:$isChecked")
                }
            }
        })


        adapter.addItem(object : ExpandListAdapter.ItemStruct {
            override fun getTitle(): String {
                return "title2"
            }

            override fun getExpandView(): View? {
                val subTv = TextView(this@MainActivity)
                subTv.text = "test subTv2"
                return subTv
            }

            override fun getSwitchListener(): CompoundButton.OnCheckedChangeListener? {
                return CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    Log.d(TAG,"isChecked:$isChecked")
                }
            }
        })


        mList.adapter = adapter
        mList.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


}