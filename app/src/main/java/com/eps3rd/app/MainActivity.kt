package com.eps3rd.app


import android.content.Intent
import android.content.res.Configuration
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
import com.eps3rd.pixiv.IFragment
import com.eps3rd.pixiv.fragment.CollectionFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tencent.mmkv.MMKV
import java.lang.Exception
import com.eps3rd.pixiv.Constants as PixivConstants


@Route(path = Constants.MAIN_ACTIVITY)
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "eps3rd->MainActivity"
        private const val KEY_DRAWER_ITEM = "KEY_DRAWER_ITEM"
        private const val DEFAULT_DRAWER_ITEM = "User,Control,Debug"
        private const val KEY_DRAWER_CONTROL_SWITCH = "KEY_DRAWER_CONTROL_SWITCH"
        private const val KEY_DRAWER_CONTROL_ITEM = "KEY_DRAWER_CONTROL_ITEM"
        private const val KEY_DRAWER_ITEM_EXPAND = "KEY_DRAWER_ITEM_EXPAND"
        private const val DEFAULT_DRAWER_CONTROL_ITEM = "HOME,BLANK"
        private const val DEFAULT_DRAWER_ITEM_EXPAND = "true,true,true"
    }

    private lateinit var mDrawerHeaderContainer: ViewGroup
    private lateinit var mDrawerHeader: ImageView
    private lateinit var mDrawerList: RecyclerView
    private lateinit var mUserImage: ImageView
    private lateinit var mBottomButton: View
    private lateinit var mBottomArea: TabLayout
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mViewPager: ViewPager2
    private lateinit var mViewPagerAdapter: MainActivityPagerAdapter
    private var mBottomButtonControlSwitch: SwitchMaterial? = null


    private var mDrawerItemPrefString: String = DEFAULT_DRAWER_ITEM
    private var mDrawerControlPrefString: String = DEFAULT_DRAWER_CONTROL_ITEM

    private val mDrawerItemAdapter = ExpandListAdapter()
    private val mControlItemAdapter = ControlItemAdapter()


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

        mViewPagerAdapter.addItemFirst(fragment)
        mDrawerLayout.closeDrawer(Gravity.LEFT)
        mViewPager.post { mViewPager.currentItem = 0 }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDrawerHeaderContainer = findViewById<ViewGroup>(R.id.drawer_header_container)
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
        mDrawerHeaderContainer.setOnClickListener {
            startActivity(Intent(this@MainActivity,SplashActivity::class.java))
        }

        mViewPager = findViewById<ViewPager2>(R.id.view_pager)
        mViewPagerAdapter = MainActivityPagerAdapter(
            supportFragmentManager,
            lifecycle
        )
        mViewPager.adapter = mViewPagerAdapter


        loadPreference()

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

        setupStatusBar()
        setupDrawer()
        setupBottomAndPager()

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, mViewPager) { tab, position ->
            tab.text =
                resources.getString((mViewPagerAdapter.getItem(position) as IFragment).getFragmentTitle())
        }.attach()

        Log.d(TAG,"onCreate()")
    }

    override fun onDestroy() {
        val kv: MMKV = MMKV.defaultMMKV()!!
        val builder = StringBuilder()
        for (item in mDrawerItemAdapter.mTagList) {
            builder.append(item).append(",")
        }
        builder.deleteCharAt(builder.lastIndex)
        mDrawerItemPrefString = builder.toString()
        kv.encode(KEY_DRAWER_ITEM, mDrawerItemPrefString)

        builder.clear()
        for (item in mControlItemAdapter.mTagList) {
            builder.append(item).append(",")
        }
        builder.deleteCharAt(builder.lastIndex)
        mDrawerControlPrefString = builder.toString()
        kv.encode(KEY_DRAWER_CONTROL_ITEM, mDrawerControlPrefString)

        mBottomButtonControlSwitch?.let {
            kv.encode(KEY_DRAWER_CONTROL_SWITCH, it.isChecked)
        }

        try {
            builder.clear()
            for (i in 0 until mDrawerItemAdapter.itemCount){
                val v = mDrawerList.findViewHolderForAdapterPosition(i) as ExpandListAdapter.VH
                builder.append(v.mExpanded).append(",")
            }
        }catch (e: Exception){
            Log.d(TAG,"drawer list does not have any. ${Log.getStackTraceString(e)}")
        }

        builder.deleteCharAt(builder.lastIndex)
        val mDrawerItemExpandString = builder.toString()
        Log.d(TAG, "onDestroy:$mDrawerItemPrefString,$mDrawerControlPrefString,$mDrawerItemExpandString")
        kv.encode(KEY_DRAWER_ITEM_EXPAND, mDrawerItemExpandString)
        super.onDestroy()
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG,"onStart")
    }


    override fun onStop() {
        super.onStop()
        Log.d(TAG,"onStop")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG,"onConfigurationChanged")
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        }else{

        }
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
            mViewPagerAdapter.addItemFirst(fragment)
            mViewPager.post { mViewPager.currentItem = 0 }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "KEYCODE_BACK")
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT)
                return true
            }
            if (mViewPagerAdapter.getItem(0) != mViewPagerAdapter.mFirstFragment) {
                mViewPagerAdapter.removeToFirst()
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
            mDrawerItemAdapter.addItem(DrawerItemProvider.mMap.get(item)!!)
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

    private fun setupBottomAndPager() {
        val items = mDrawerControlPrefString.split(",")
        for (item in items) {
            DrawerControlProvider.mMap.get(item)!!.apply {
                mControlItemAdapter.addItem(this)
                mBottomArea.addTab(mBottomArea.newTab().setText(this.title).setTag(this.tag))

                val fragment: Fragment =
                    ARouter.getInstance().build(this.fragmentPath)
                        .navigation() as Fragment
                mViewPagerAdapter.addItem(fragment)

            }
        }
        mBottomArea.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.d(TAG, "onTabUnselected:${tab?.tag}")
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val selectTo = mControlItemAdapter.mTagList.indexOf(tab!!.tag as String)
                mViewPagerAdapter.removeToFirst()
                mViewPager.post { mViewPager.setCurrentItem(selectTo, false) }
            }
        })
    }

    private fun loadPreference() {
        val kv: MMKV = MMKV.defaultMMKV()!!
        if (kv.containsKey(KEY_DRAWER_ITEM)) {
            mDrawerItemPrefString = kv.decodeString(KEY_DRAWER_ITEM).toString()
            Log.d(TAG, "loadPreference: drawer items:${mDrawerItemPrefString}")
        }
        if (kv.containsKey(KEY_DRAWER_CONTROL_ITEM)) {
            mDrawerControlPrefString = kv.decodeString(KEY_DRAWER_CONTROL_ITEM).toString()
            Log.d(TAG, "loadPreference: control items:${mDrawerControlPrefString}")
        }

        if (kv.containsKey(KEY_DRAWER_ITEM_EXPAND)){
            val  mDrawerItemExpandString = kv.decodeString(KEY_DRAWER_ITEM_EXPAND).toString()
            val expands = mDrawerItemExpandString.split(",")
            mDrawerItemAdapter.mExpandPrefString = expands
        }

        initControlItem()
        initDrawerItem()
    }

    private fun initDrawerItem() {
        DrawerItemProvider.mMap["User"] = object : ExpandListAdapter.ItemStruct {
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
        DrawerItemProvider.mMap["Control"] = object : ExpandListAdapter.ItemStruct {
            override fun getTitle(): String {
                return resources.getString(R.string.drawer_item_control)
            }

            override fun getItemTag(): String {
                return "Control"
            }

            override fun getExpandView(): View? {
                val container = layoutInflater?.inflate(R.layout.item_expand_control, null)
                mBottomButtonControlSwitch =
                    container.findViewById<SwitchMaterial>(R.id.switch_disable_bottom)

                mBottomButtonControlSwitch?.setOnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
                    mBottomButton.visibility = if (isChecked) View.GONE else View.VISIBLE
                }
                val kv: MMKV = MMKV.defaultMMKV()!!
                mBottomButtonControlSwitch?.isChecked = kv.decodeBool(KEY_DRAWER_CONTROL_SWITCH)

                container.findViewById<RecyclerView>(R.id.rv_fragment_sort).apply {
                    this.adapter = mControlItemAdapter
                    mControlItemAdapter.mTouchHelper.attachToRecyclerView(this)
                    this.layoutManager = LinearLayoutManager(context)
                }
                return container
            }

            override fun getSwitchListener(): CompoundButton.OnCheckedChangeListener? {
                return null
            }
        }
        DrawerItemProvider.mMap["Debug"] = object : ExpandListAdapter.ItemStruct {
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

    private fun initControlItem() {
        DrawerControlProvider.mMap["HOME"] =
            ControlItemAdapter.ControlItem(resources.getString(R.string.fragment_home))
                .apply {
                    tag = "HOME"
                    fragmentPath = com.eps3rd.pixiv.Constants.FRAGMENT_PATH_HOME
                }
        DrawerControlProvider.mMap["BLANK"] =
            ControlItemAdapter.ControlItem(resources.getString(R.string.fragment_blank))
                .apply {
                    tag = "BLANK"
                    fragmentPath = com.eps3rd.pixiv.Constants.FRAGMENT_PATH_BLANK
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

    private object DrawerItemProvider {
        val mMap = HashMap<String, ExpandListAdapter.ItemStruct>()
    }

    private object DrawerControlProvider {
        val mMap = HashMap<String, ControlItemAdapter.ControlItem>()
    }


}