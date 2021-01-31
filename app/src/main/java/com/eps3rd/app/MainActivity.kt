package com.eps3rd.app


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.eps3rd.baselibrary.Constants
import com.eps3rd.pixiv.GlideCircleBorderTransform
import com.eps3rd.pixiv.Constants as PixivConstants
import com.eps3rd.pixiv.fragment.HomeFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.lang.IllegalStateException

@Route(path = Constants.MAIN_ACTIVITY)
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mDrawerHeader: ImageView
    private lateinit var mDrawerList: RecyclerView
    private lateinit var mUserImage: ImageView
    private lateinit var mBottomButton: View
    private lateinit var mBottomArea: ViewGroup
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var  viewPager: ViewPager2
    private lateinit var mViewPagerAdapter: MainActivityPagerAdapter

    private val mDrawerItemClickListener: View.OnClickListener = View.OnClickListener {
        val fragment: Fragment = when(it.tag){
            DrawerItemTAG.ITEM_COLLECTION -> {
                ARouter.getInstance().build(PixivConstants.FRAGMENT_PATH_COLLECTION)
                    .navigation() as Fragment
            }

            DrawerItemTAG.ITEM_FOLLOWING -> {
                    ARouter.getInstance().build(PixivConstants.FRAGMENT_PATH_FOLLOWING)
                        .navigation() as Fragment
            }
            else ->{
                throw IllegalStateException("NO SUCH TAG")
            }
        }

        mViewPagerAdapter.addItem(fragment)
        drawerLayout.closeDrawer(Gravity.LEFT)
        viewPager.post {viewPager.currentItem = 0}

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDrawerHeader = findViewById(R.id.drawer_header)
        mDrawerList = findViewById(R.id.drawer_list)
        mUserImage = findViewById(R.id.drawer_user_img)
        mBottomButton = findViewById(R.id.bottom_button)
        mBottomArea = findViewById(R.id.main_bottom)

        mBottomButton.setOnClickListener {
            if (mBottomArea.visibility == View.GONE){
                BottomAreaBehavior.show(mBottomArea)
            }else{
                BottomAreaBehavior.hide(mBottomArea)
            }
        }

        setupStatusBar()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupDrawer()


        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        viewPager = findViewById<ViewPager2>(R.id.view_pager)
        mViewPagerAdapter = MainActivityPagerAdapter(
            supportFragmentManager,
            lifecycle
        )
        viewPager.adapter = mViewPagerAdapter
        try {
            val fragments = arrayOf(PixivConstants.FRAGMENT_PATH_BLANK, PixivConstants.FRAGMENT_PATH_HOME)
            val param = Bundle()
            param.putString("param1", "t1")
            param.putString("param2", "t2")
            val params = arrayOf(param,null)

            for ((f,param) in fragments zip params) {
                val fragment: Fragment =
                    ARouter.getInstance().build(f)
                        .with(param)
                        .navigation() as Fragment
                mViewPagerAdapter.addItem(fragment)
            }
        } catch (e: Exception) {
            Log.d(TAG, "error to add fragments${Log.getStackTraceString(e)}")
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "OBJECT ${(position + 1)}"
        }.attach()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG,"onNewIntent")
        intent?.let {
            val f = it.getStringExtra(Constants.MAIN_ACTIVITY_REQUEST) ?: return
            val fragment: Fragment =
                ARouter.getInstance()
                    .build(f)
                    .navigation() as Fragment
            mViewPagerAdapter.addItem(fragment)
            viewPager.post {viewPager.currentItem = 0}
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Log.d(TAG,"KEYCODE_BACK")
            if (mViewPagerAdapter.getItem(0) !is HomeFragment){
                mViewPagerAdapter.removeItem(0)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun setupStatusBar() {
//        window.statusBarColor = Color.TRANSPARENT
    }

    private fun setupDrawer() {
        val adapter = ExpandListAdapter()
        adapter.addItem(object : ExpandListAdapter.ItemStruct {
            override fun getTitle(): String {
                return "User"
            }

            override fun getExpandView(): View? {
                val listView = layoutInflater.inflate(R.layout.item_expand_user,null)
                listView.findViewById<View>(R.id.line2).apply {
                    tag = DrawerItemTAG.ITEM_COLLECTION
                    setOnClickListener(mDrawerItemClickListener)
                }
                listView.findViewById<View>(R.id.line3).apply {
                    tag = DrawerItemTAG.ITEM_FOLLOWING
                    setOnClickListener(mDrawerItemClickListener)
                }
                return listView
            }

            override fun getSwitchListener(): CompoundButton.OnCheckedChangeListener? {
                return CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    Log.d(TAG, "isChecked:$isChecked")
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
                    Log.d(TAG, "isChecked:$isChecked")
                }
            }
        })


        adapter.mTouchHelper.attachToRecyclerView(mDrawerList)
        mDrawerList.adapter = adapter
        mDrawerList.layoutManager = LinearLayoutManager(this)

        Glide.with(this).
            load(Uri.parse("android.resource://com.eps3rd.pixiv/"+ R.drawable.bg_drawer_item)).
            transform(FitCenter(),
                GlideCircleBorderTransform(
                    4,
                    resources.getColor(R.color.color_primary)
                )
            ).
            into(mUserImage)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    data class UserData(var uid: Int){
        var userImage: Uri? = null
        var userName: String = ""
    }
}