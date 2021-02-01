package com.eps3rd.app


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.Menu
import android.view.View
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
import com.eps3rd.pixiv.IFragment
import com.eps3rd.pixiv.fragment.CollectionFragment
import com.eps3rd.pixiv.fragment.HomeFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tencent.mmkv.MMKV
import com.eps3rd.pixiv.Constants as PixivConstants

@Route(path = Constants.MAIN_ACTIVITY)
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val KEY_DRAWER_ITEM = "KEY_DRAWER_ITEM"
        private const val DEFAULT_DRAWER_ITEM = "User,Control,Debug"
    }

    private lateinit var mDrawerHeader: ImageView
    private lateinit var mDrawerList: RecyclerView
    private lateinit var mUserImage: ImageView
    private lateinit var mBottomButton: View
    private lateinit var mBottomArea: TabLayout
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mViewPager: ViewPager2
    private lateinit var mViewPagerAdapter: MainActivityPagerAdapter
    private var mDrawerItemPrefString: String = DEFAULT_DRAWER_ITEM
    private val mDrawerItemAdapter = ExpandListAdapter()

    private val mDrawerItemClickListener: View.OnClickListener = View.OnClickListener {
        val fragment: Fragment = when (it.tag) {
            DrawerItemTAG.ITEM_COLLECTION -> {
                ARouter.getInstance().build(PixivConstants.FRAGMENT_PATH_COLLECTION)
                    .withString(CollectionFragment.TYPE, CollectionFragment.TYPE_COLLECTION)
                    .navigation() as Fragment
            }

            DrawerItemTAG.ITEM_FOLLOWING -> {
                ARouter.getInstance().build(PixivConstants.FRAGMENT_PATH_FOLLOWING)
                    .navigation() as Fragment
            }
            else -> {
                throw IllegalStateException("NO SUCH TAG")
            }
        }

        mViewPagerAdapter.addItem(fragment)
        mDrawerLayout.closeDrawer(Gravity.LEFT)
        mViewPager.post { mViewPager.currentItem = 0 }

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
            if (mBottomArea.visibility == View.GONE) {
                BottomAreaBehavior.show(mBottomArea)
            } else {
                BottomAreaBehavior.hide(mBottomArea)
            }
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)
        setSupportActionBar(toolbar)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )
        mDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        loadPreference()

        setupStatusBar()
        setupDrawer()
        setupBottom()

        mViewPager = findViewById<ViewPager2>(R.id.view_pager)
        mViewPagerAdapter = MainActivityPagerAdapter(
            supportFragmentManager,
            lifecycle
        )
        mViewPager.adapter = mViewPagerAdapter

        try {
            val fragments =
                arrayOf(PixivConstants.FRAGMENT_PATH_BLANK, PixivConstants.FRAGMENT_PATH_HOME)
            val param = Bundle()
            param.putString("param1", "t1")
            param.putString("param2", "t2")
            val params = arrayOf(param, null)

            for ((f, param) in fragments zip params) {
                val fragment: Fragment =
                    ARouter.getInstance().build(f)
                        .with(param)
                        .navigation() as Fragment
                mViewPagerAdapter.addItem(fragment)
            }
        } catch (e: Exception) {
            Log.d(TAG, "error to add fragments${Log.getStackTraceString(e)}")
        }

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, mViewPager) { tab, position ->
            tab.text =resources.getString( (mViewPagerAdapter.getItem(position) as IFragment).getFragmentTitle())
        }.attach()
    }

    override fun onStop() {
        val kv: MMKV = MMKV.defaultMMKV()!!
        val builder = StringBuilder()
        for (item in mDrawerItemAdapter.mTagList) {
            builder.append(item).append(",")
        }
        builder.deleteCharAt(builder.lastIndex)
        mDrawerItemPrefString = builder.toString()
        Log.d(TAG, "onStop:DrawerItemPref $mDrawerItemPrefString")
        kv.encode(KEY_DRAWER_ITEM, mDrawerItemPrefString)
        super.onStop()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            val f = it.getBundleExtra(Constants.MAIN_ACTIVITY_REQUEST) ?: return
            val fragment: Fragment =
                ARouter.getInstance()
                    .build(f.getString(Constants.MAIN_ACTIVITY_START_FRAGMENT))
                    .with(f.getBundle(Constants.MAIN_ACTIVITY_START_FRAGMENT_PARAM))
                    .navigation() as Fragment

            Log.d(TAG, "onNewIntent:$fragment")
            mViewPagerAdapter.addItem(fragment)
            mViewPager.post { mViewPager.currentItem = 0 }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "KEYCODE_BACK")
            mDrawerLayout.closeDrawer(Gravity.LEFT)
            if (mViewPagerAdapter.getItem(0) !is HomeFragment) {
                mViewPagerAdapter.removeItem(0)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d(TAG, "onTrimMemory:$level")
    }


    private fun setupStatusBar() {
//        window.statusBarColor = Color.TRANSPARENT
    }

    private fun setupDrawer() {
        val items = mDrawerItemPrefString.split(",")

        for (item in items) {
            mDrawerItemAdapter.addItem(DrawerItemProviderImpl.mMap.get(item)!!)
        }

        mDrawerItemAdapter.mTouchHelper.attachToRecyclerView(mDrawerList)
        mDrawerList.adapter = mDrawerItemAdapter
        mDrawerList.layoutManager = LinearLayoutManager(this)

        Glide.with(this)
            .load(Uri.parse("android.resource://com.eps3rd.pixiv/" + R.drawable.bg_drawer_item))
            .transform(
                FitCenter(),
                GlideCircleBorderTransform(
                    4,
                    resources.getColor(R.color.color_primary)
                )
            ).into(mUserImage)
    }

    private fun setupBottom() {
        mBottomArea.addTab(mBottomArea.newTab().setText("test0").setTag("hello"))
        mBottomArea.addTab(mBottomArea.newTab().setText("test2").setTag("hello2"))
        mBottomArea.addTab(mBottomArea.newTab().setText("test3").setTag("hello3"))
        mBottomArea.addTab(mBottomArea.newTab().setText("test4").setTag("hello4"))

        mBottomArea.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d(TAG, "onTabReselected:${tab?.tag}")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.d(TAG, "onTabUnselected:${tab?.tag}")
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d(TAG, "onTabSelected:${tab?.tag}")
            }
        })
    }

    private fun loadPreference() {
        initDrawerItem()
        val kv: MMKV = MMKV.defaultMMKV()!!
        if (kv.containsKey(KEY_DRAWER_ITEM) == true) {
            mDrawerItemPrefString = kv.decodeString(KEY_DRAWER_ITEM).toString()
            Log.d(TAG, "loadPreference: drawer items:${mDrawerItemPrefString}")
        }
    }

    private fun initDrawerItem() {
        DrawerItemProviderImpl.mMap["User"] = object : ExpandListAdapter.ItemStruct {
            override fun getTitle(): String {
                return resources.getString(R.string.drawer_item_user)
            }

            override fun getItemTag(): String {
                return "User"
            }

            override fun getExpandView(): View? {
                val listView = layoutInflater?.inflate(R.layout.item_expand_user, null)
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
        }
        DrawerItemProviderImpl.mMap["Control"] = object : ExpandListAdapter.ItemStruct {
            override fun getTitle(): String {
                return resources.getString(R.string.drawer_item_control)
            }

            override fun getItemTag(): String {
                return "Control"
            }

            override fun getExpandView(): View? {
                val container = layoutInflater?.inflate(R.layout.item_expand_control, null)

                return container
            }

            override fun getSwitchListener(): CompoundButton.OnCheckedChangeListener? {
                return null
            }
        }

        DrawerItemProviderImpl.mMap["Debug"] = object : ExpandListAdapter.ItemStruct {
            override fun getTitle(): String {
                return resources.getString(R.string.drawer_item_debug)
            }

            override fun getItemTag(): String {
                return "Debug"
            }

            override fun getExpandView(): View? {
                val tv = TextView(this@MainActivity)
                tv.setText("test")
                return tv
            }

            override fun getSwitchListener(): CompoundButton.OnCheckedChangeListener? {
                return CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    Log.d(TAG, "isChecked:$isChecked")
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    data class UserData(var uid: Int) {
        var userImage: Uri? = null
        var userName: String = ""
    }

    interface DrawerItemProvider {
        fun getDrawerItemByTitle(title: String): ExpandListAdapter.ItemStruct
    }


    private object DrawerItemProviderImpl : DrawerItemProvider {
        val mMap = HashMap<String, ExpandListAdapter.ItemStruct>()

        override fun getDrawerItemByTitle(title: String): ExpandListAdapter.ItemStruct {
            return mMap[title]!!
        }
    }

}