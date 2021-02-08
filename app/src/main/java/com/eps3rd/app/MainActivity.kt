package com.eps3rd.app

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.eps3rd.app.adapter.ControlItemAdapter
import com.eps3rd.app.adapter.ExpandListAdapter
import com.eps3rd.app.adapter.MainActivityPagerAdapter
import com.eps3rd.app.transaction.SearchSuggestionViewPresenter
import com.eps3rd.app.transaction.UserHeadPresenter
import com.eps3rd.app.ui.BottomAreaBehavior
import com.eps3rd.baselibrary.Constants
import com.eps3rd.pixiv.GlideSettingsModule
import com.eps3rd.pixiv.IFragment
import com.eps3rd.pixiv.api.UserHandle
import com.eps3rd.pixiv.fragment.CollectionFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.eps3rd.pixiv.Constants as PixivConstants


@Route(path = Constants.MAIN_ACTIVITY)
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "eps3rd->MainActivity"
        private const val KEY_DRAWER_ITEM = "KEY_DRAWER_ITEM"
        private const val DEFAULT_DRAWER_ITEM = "User,Control,Cache,Debug"
        private const val KEY_DRAWER_CONTROL_SWITCH_BOTTOM = "KEY_DRAWER_CONTROL_SWITCH_BOTTOM"
        private const val KEY_DRAWER_CONTROL_SWITCH_TOP = "KEY_DRAWER_CONTROL_SWITCH_TOP"
        private const val KEY_DRAWER_CONTROL_ITEM = "KEY_DRAWER_CONTROL_ITEM"
        private const val KEY_DRAWER_ITEM_EXPAND = "KEY_DRAWER_ITEM_EXPAND"
        private const val DEFAULT_DRAWER_CONTROL_ITEM = "HOME,BLANK"
    }

    private lateinit var mUserHeadPresenter: UserHeadPresenter

    private lateinit var mDrawerList: RecyclerView
    private lateinit var mBottomButton: View
    private lateinit var mBottomArea: TabLayout
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mViewPager: ViewPager2
    private lateinit var mViewPagerAdapter: MainActivityPagerAdapter
    private lateinit var mTabLayout: TabLayout
    private var mBottomButtonControlSwitch: SwitchMaterial? = null
    private var mTopButtonControlSwitch: SwitchMaterial? = null
    private lateinit var mSuggestionViewPresenter: SearchSuggestionViewPresenter

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
                throw IllegalStateException("NO SUCH TAG:${it.tag}")
            }
        }
        mViewPagerAdapter.addItemFirst(fragment)
        mDrawerLayout.closeDrawer(Gravity.LEFT)
        mViewPager.post { mViewPager.currentItem = 0 }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mUserHeadPresenter = UserHeadPresenter(findViewById(android.R.id.content))
        mDrawerList = findViewById(R.id.drawer_list)
        mBottomButton = findViewById(R.id.bottom_button)
        mBottomArea = findViewById(R.id.main_bottom)
        mTabLayout = findViewById<TabLayout>(R.id.tab_layout)
        mSuggestionViewPresenter =
            SearchSuggestionViewPresenter(
                findViewById(
                    R.id.suggestion_view_container
                )
            )

        mBottomButton.setOnClickListener {
            if (mBottomArea.visibility == View.GONE) {
                BottomAreaBehavior.show(mBottomArea)
            } else {
                BottomAreaBehavior.hide(mBottomArea)
            }
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


        TabLayoutMediator(mTabLayout, mViewPager) { tab, position ->
            tab.text =
                resources.getString((mViewPagerAdapter.getItem(position) as IFragment).getFragmentTitle())
        }.attach()

        Log.d(TAG, "onCreate()")
    }

    override fun onDestroy() {
        val kv: MMKV = UserHandle.kv!!
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
            kv.encode(KEY_DRAWER_CONTROL_SWITCH_BOTTOM, it.isChecked)
        }
        mTopButtonControlSwitch?.let {
            kv.encode(KEY_DRAWER_CONTROL_SWITCH_TOP, it.isChecked)
        }

        try {
            builder.clear()
            for (i in 0 until mDrawerItemAdapter.itemCount) {
                val v = mDrawerList.findViewHolderForAdapterPosition(i) as ExpandListAdapter.VH
                builder.append(v.mExpanded).append(",")
            }
        } catch (e: Exception) {
            Log.d(TAG, "drawer list does not have any. ${Log.getStackTraceString(e)}")
        }

        builder.deleteCharAt(builder.lastIndex)
        val mDrawerItemExpandString = builder.toString()
        Log.d(
            TAG,
            "onDestroy:$mDrawerItemPrefString,$mDrawerControlPrefString,$mDrawerItemExpandString"
        )
        kv.encode(KEY_DRAWER_ITEM_EXPAND, mDrawerItemExpandString)

        super.onDestroy()
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }


    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged")
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        } else {

        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "intent:$intent")
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

        mUserHeadPresenter.setUp()
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

        if (kv.containsKey(KEY_DRAWER_ITEM_EXPAND)) {
            val mDrawerItemExpandString = kv.decodeString(KEY_DRAWER_ITEM_EXPAND).toString()
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

                mTopButtonControlSwitch =
                    container.findViewById<SwitchMaterial>(R.id.switch_disable_top)
                mTopButtonControlSwitch?.setOnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
                    mTabLayout.visibility = if (isChecked) View.GONE else View.VISIBLE
                }

                val kv: MMKV = MMKV.defaultMMKV()!!
                mBottomButtonControlSwitch?.isChecked =
                    kv.decodeBool(KEY_DRAWER_CONTROL_SWITCH_BOTTOM)
                mTopButtonControlSwitch?.isChecked =
                    kv.decodeBool(KEY_DRAWER_CONTROL_SWITCH_TOP)

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
        DrawerItemProvider.mMap["Cache"] = object : ExpandListAdapter.ItemStruct {
            val tv = TextView(this@MainActivity)

            override fun getTitle(): String {
                return resources.getString(R.string.drawer_item_cache)
            }

            override fun getItemTag(): String {
                return "Cache"
            }

            override fun getExpandView(): View? {
                GlobalScope.launch(Dispatchers.Main) {
                    tv.text = GlideSettingsModule.getCacheSize(application)
                }
                return tv
            }

            override fun getSwitchListener(): CompoundButton.OnCheckedChangeListener? {
                return CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        buttonView.isEnabled = false
                        GlobalScope.launch(Dispatchers.Main) {
                            val success = GlideSettingsModule.clearCacheDiskSelf(application)
                            if (success) {
                                buttonView.isChecked = false
                                buttonView.isEnabled = true
                                tv.text = GlideSettingsModule.getCacheSize(application)
                            }
                            Log.d(TAG, "clear cache:$success")
                        }
                    }
                }
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
        menuInflater.inflate(R.menu.main, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val mSearchView: SearchView = MenuItemCompat.getActionView(searchItem) as SearchView
        mSearchView.setIconifiedByDefault(true)
        mSearchView.isSubmitButtonEnabled = true
        mSearchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        mSearchView.isIconified = true
        mSearchView.queryHint = resources.getString(R.string.query_hint)
        mSearchView.clearFocus()
        mSearchView.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                // show rv with adapter
                mSuggestionViewPresenter.showSuggestion()
            } else {
                // hide rv, save suggestion
                mSuggestionViewPresenter.hideAndSaveSuggestion()
            }
        }
        mSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d(TAG, "query: $query")
                mSuggestionViewPresenter.addItem(query)
                mSearchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.d(TAG, "onQueryTextChange")
                mSuggestionViewPresenter.addFilterString(newText)
                return false
            }
        })

        mSuggestionViewPresenter.mClickListener = object :
            SearchSuggestionViewPresenter.OnClickListener {
            override fun onClick(query: String) {
                mSearchView.setQuery(query, false)
            }
        }
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
