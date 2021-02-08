package com.eps3rd.pixiv.fragment

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.load.model.GlideUrl
import com.eps3rd.app.R
import com.eps3rd.pixiv.Constants
import com.eps3rd.pixiv.IFragment
import com.eps3rd.pixiv.ImageCardAdapter
import com.eps3rd.pixiv.api.UserHandle
import com.eps3rd.pixiv.model.ListIllust
import com.eps3rd.pixiv.models.IllustsBean
import com.eps3rd.pixiv.util.GlideUrlChild
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception


@Route(path = Constants.FRAGMENT_PATH_HOME)
class HomeFragment : Fragment(), IFragment {

    companion object {
        const val TAG = "HomeFragment"
    }

    private lateinit var mRecommendRV: RecyclerView
    private val mRecommendAdapter = ImageCardAdapter()


    private lateinit var mRankRv: RecyclerView
    private val mRankAdapter = ImageCardAdapter()

    private lateinit var mRefreshLayout: SmartRefreshLayout
    private var mListIllust: ListIllust? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        mRecommendRV = view.findViewById(R.id.rv_recommended)
        mRecommendRV.layoutManager = GridLayoutManager(context, 2)
        mRecommendAdapter.mShowAuthor = false
        mRecommendAdapter.mShowOverlay = false
        mRecommendRV.adapter = mRecommendAdapter

        mRankRv = view.findViewById(R.id.rv_rank)
        mRankRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        mRankRv.adapter = mRankAdapter

        mRefreshLayout = view.findViewById(R.id.recommended_refresh)
        mRefreshLayout.setRefreshHeader(ClassicsHeader(context))
        mRefreshLayout.setRefreshFooter(ClassicsFooter(context))
        mRefreshLayout.setOnRefreshListener {
            GlobalScope.launch(Dispatchers.Default) {
                try {
                    val response =
                        UserHandle.api.getRecmdIllust(UserHandle.userModel!!.response.access_token)
                    if (response == null) {
                        mRefreshLayout.finishRefresh()
                        return@launch
                    }
                    response.body()?.let {
                        mListIllust = it
                        Log.d(TAG, "refresh:${it.illusts.size}")
                        bindAdapterData(mRecommendAdapter, it.illusts, true)
                        bindAdapterData(mRankAdapter, it.ranking_illusts, true)
                        mRefreshLayout.finishRefresh()
                    }
                } catch (e: Exception) {
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
                        mRefreshLayout.finishLoadMore()
                        Log.d(TAG, "LoadMore-> response is null")
                        return@launch
                    }
                    response.body()?.let {
                        mListIllust = it
                        Log.d(TAG, "load more:${it.illusts.size}")
                        bindAdapterData(mRecommendAdapter, it.illusts)
                        bindAdapterData(mRankAdapter, it.ranking_illusts)
                        mRefreshLayout.finishLoadMore()
                    }
                    mRefreshLayout.finishLoadMore()
                }
            }
        }
        mRefreshLayout.autoRefresh()
        return view
    }

    private suspend fun bindAdapterData(
        adapter: ImageCardAdapter,
        list: List<IllustsBean>,
        refresh: Boolean = false
    ) {
        if (refresh) {
            withContext(Dispatchers.Main) {
                adapter.clearItem()
            }
        }
        for (item in list) {
            val imgCard = ImageCardAdapter.CardStruct()
            imgCard.imageBean = item
            imgCard.imgUri = GlideUrlChild(item.image_urls.square_medium)
            imgCard.imageTitle = item.title
            imgCard.imageCount = item.page_count.toString()
            imgCard.authorName = item.user.name
            imgCard.authorIcon = GlideUrlChild(item.user.profile_image_urls.getMaxImage())
            withContext(Dispatchers.Main) {
                adapter.addItem(imgCard)
            }
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged")
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecommendRV.layoutManager = GridLayoutManager(context, 2)
        } else {
            mRecommendRV.layoutManager = GridLayoutManager(context, 4)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun getFragmentTitle(): Int {
        return R.string.fragment_home
    }
}