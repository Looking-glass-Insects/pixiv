package com.eps3rd.pixiv.fragment

import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.eps3rd.pixiv.Constants
import com.eps3rd.pixiv.ImageCardAdapter
import com.eps3rd.app.R
import com.eps3rd.pixiv.IFragment


@Route(path = Constants.FRAGMENT_PATH_HOME)
class HomeFragment : Fragment(), IFragment {

    companion object{
        const val TAG = "HomeFragment"
    }

    private lateinit var mRecommendRV: RecyclerView
    private val mRecommendAdapter = ImageCardAdapter()


    private lateinit var mRankRv: RecyclerView
    private val mRankAdapter = ImageCardAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"onCreateView")
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        mRecommendRV = view.findViewById(R.id.rv_recommended)
        mRecommendRV.layoutManager = GridLayoutManager(context,2)
        mRecommendAdapter.mShowAuthor = false
        mRecommendAdapter.mShowOverlay = false
        mRecommendRV.adapter = mRecommendAdapter

        mRankRv = view.findViewById(R.id.rv_rank)
        mRankRv.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        mRankRv.adapter = mRankAdapter
        return view
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG,"onConfigurationChanged")
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecommendRV.layoutManager = GridLayoutManager(context,2)
        }else{
            mRecommendRV.layoutManager = GridLayoutManager(context,4)
        }
    }

    override fun onResume() {
        super.onResume()
        // DEBUG CODE
        mRecommendAdapter.addItem(ImageCardAdapter.CardStruct(Uri.parse("android.resource://com.eps3rd.pixiv/"+ R.mipmap.test)))
        mRankAdapter.addItem(ImageCardAdapter.CardStruct(Uri.parse("android.resource://com.eps3rd.pixiv/"+ R.mipmap.test)))
        // DEBUG CODE
    }

    override fun getFragmentTitle(): Int {
        return R.string.fragment_home
    }

}