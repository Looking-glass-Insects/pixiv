package com.eps3rd.pixiv.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.eps3rd.app.R
import com.eps3rd.pixiv.Constants
import com.eps3rd.pixiv.IFragment
import com.eps3rd.pixiv.adapter.BaseHeaderAdapter
import com.eps3rd.pixiv.adapter.HomeHeaderAdapter
import com.eps3rd.pixiv.adapter.ImageCardAdapter
import com.eps3rd.pixiv.api.UserHandle
import com.eps3rd.pixiv.model.ListIllust
import com.eps3rd.pixiv.models.IllustsBean
import com.eps3rd.pixiv.util.GlideUrlChild
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


@Route(path = Constants.FRAGMENT_PATH_HOME)
class HomeFragment : Fragment(), IFragment {

    companion object {
        const val TAG = "HomeFragment"
    }

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: BaseHeaderAdapter<HomeHeaderAdapter.HeaderVH, ImageCardAdapter.ImageCardVH>

    private lateinit var mRefreshLayout: SmartRefreshLayout
    private var mListIllust: ListIllust? = null
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var mEmptyLayout: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = BaseHeaderAdapter()
        val norm = ImageCardAdapter()
        norm.mShowAuthor = false
        mAdapter.normalAdapter = norm
        mAdapter.mHeaderPresenter = HomeHeaderAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        mRecyclerView = view.findViewById(R.id.recycler_view)
        layoutManager = GridLayoutManager(mRecyclerView.context, 4)
        mEmptyLayout = view.findViewById(R.id.layout_empty)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) {
                    4
                } else {
                    2
                }
            }
        }

        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.adapter = mAdapter

        mRefreshLayout = view.findViewById(R.id.recommended_refresh)
        mRefreshLayout.setRefreshHeader(ClassicsHeader(context))
        mRefreshLayout.setRefreshFooter(ClassicsFooter(context))
        mRefreshLayout.setOnRefreshListener {
            GlobalScope.launch(Dispatchers.Default) {
                try {
                    val response =
                        UserHandle.api.getRecmdIllust(UserHandle.userModel!!.response.access_token)
                    if (response == null) {
                        showEmpty(true)
                        mRefreshLayout.finishRefresh(false)
                        return@launch
                    }
                    response.body()?.let {
                        mListIllust = it
                        Log.d(TAG, "refresh:${it.illusts.size}")
                        bindAdapterData(
                            mAdapter.normalAdapter as ImageCardAdapter,
                            it.illusts,
                            true
                        )
                        bindAdapterData(
                            (mAdapter.mHeaderPresenter as HomeHeaderAdapter).mRankAdapter,
                            it.ranking_illusts,
                            true
                        )
                        showEmpty(false)
                        mRefreshLayout.finishRefresh(true)
                    } ?: let {
                        showEmpty(true)
                        mRefreshLayout.finishRefresh(false)
                    }
                } catch (e: Exception) {
                    showEmpty(true)
                    mRefreshLayout.finishRefresh(false)
                    Log.d(TAG, "error:${Log.getStackTraceString(e)}")
                }
            }
        }
        mRefreshLayout.setOnLoadMoreListener {
            mListIllust?.let {
                GlobalScope.launch(Dispatchers.Default) {
                    val response =
                        UserHandle.api.getNextIllust(
                            UserHandle.userModel!!.response.access_token,
                            mListIllust!!.nextUrl
                        )
                    if (response == null) {
                        mRefreshLayout.finishLoadMore(false)
                        Log.d(TAG, "LoadMore-> response is null")
                        return@launch
                    }
                    response.body()?.let {
                        mListIllust = it
                        Log.d(TAG, "load more:${it.illusts.size}")
                        bindAdapterData(
                            mAdapter.normalAdapter as ImageCardAdapter,
                            it.illusts,
                            false,
                            true
                        )
                        bindAdapterData(
                            (mAdapter.mHeaderPresenter as HomeHeaderAdapter).mRankAdapter,
                            it.ranking_illusts, false, true
                        )
                        mRefreshLayout.finishLoadMore(true)
                    } ?: mRefreshLayout.finishLoadMore(false)
                }
            } ?: mRefreshLayout.finishLoadMore(false)

        }
        mRefreshLayout.autoRefresh()
        return view
    }


    private fun showEmpty(show: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            if (show) {
                mEmptyLayout.visibility = View.VISIBLE
                mRecyclerView.visibility = View.GONE
            } else {
                mEmptyLayout.visibility = View.GONE
                mRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun bindAdapterData(
        adapter: ImageCardAdapter,
        list: List<IllustsBean>,
        refresh: Boolean = false,
        notify: Boolean = false
    ) {
        if (refresh) {
            adapter.clearItem()
            GlobalScope.launch(Dispatchers.Main) {
                mAdapter.notifyDataSetChanged()
            }
        }
        val items = ArrayList<ImageCardAdapter.CardStruct>(100)
        for (item in list) {
            val imgCard = ImageCardAdapter.CardStruct()
            imgCard.imageBean = item
            imgCard.imgUri = GlideUrlChild(item.image_urls.square_medium)
            imgCard.imageTitle = item.title
            imgCard.imageCount = item.page_count.toString()
            imgCard.authorName = item.user.name
            imgCard.authorIcon = GlideUrlChild(item.user.profile_image_urls.getMaxImage())
            items.add(imgCard)
        }
        val itemCount = items.size
        adapter.addItems(items)
        if (notify) {
            GlobalScope.launch(Dispatchers.Main) {
                mAdapter.notifyItemRangeChanged(mAdapter.itemCount, itemCount)
            }
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged")
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == 0) {
                        4
                    } else {
                        2
                    }
                }
            }
        } else {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == 0) {
                        4
                    } else {
                        1
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun getFragmentTitle(): Int {
        return R.string.fragment_home
    }
}