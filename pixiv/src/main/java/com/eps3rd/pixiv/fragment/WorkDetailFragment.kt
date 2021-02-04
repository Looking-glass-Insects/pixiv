package com.eps3rd.pixiv.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.eps3rd.pixiv.Constants
import com.eps3rd.app.R
import com.eps3rd.pixiv.AuthorIntroAdapter
import com.eps3rd.pixiv.IFragment
import com.eps3rd.pixiv.ImageCardAdapter
import com.google.android.material.chip.ChipGroup


@Route(path = Constants.FRAGMENT_PATH_WORK_DETAIL)
class WorkDetailFragment : Fragment(), IFragment {

    companion object{
       const val TAG = "WorkDetailFragment"

        object OnCheckedListener : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                Log.d(CollectionFragment.TAG,"Tag Check:$isChecked,${buttonView?.tag}")
            }
        }
    }

    private lateinit var mImage: ImageView
    private lateinit var mTvTime: TextView
    private lateinit var mTvViews: TextView
    private lateinit var mTvLikes: TextView
    private lateinit var mTagsContainer:ChipGroup
    private lateinit var mAuthorIntroHolder: AuthorIntroAdapter.AuthorVH
    private lateinit var mRvRelatives: RecyclerView

    private val mRelativeAdapter: ImageCardAdapter = ImageCardAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        mImage = view.findViewById(R.id.image_work)
        mTvTime = view.findViewById(R.id.work_time)
        mTvViews = view.findViewById(R.id.work_views)
        mTvLikes = view.findViewById(R.id.work_likes)
        mTagsContainer = view.findViewById(R.id.tags_container)
        mAuthorIntroHolder = AuthorIntroAdapter.AuthorVH(view.findViewById(R.id.author_intro))
        mRvRelatives = view.findViewById(R.id.rv_relative)
        mRvRelatives.layoutManager = GridLayoutManager(context,2)
        mRelativeAdapter.mShowAuthor = false
        mRelativeAdapter.mShowOverlay = false
        mRvRelatives.adapter = mRelativeAdapter

        // DEBUG CODE
        addDebugData()
        mTagsContainer.addView(CollectionFragment.buildTag(inflater, "test1"))
        mTagsContainer.addView(CollectionFragment.buildTag(inflater, "test2"))
        return view
    }

    override fun onResume() {
        super.onResume()
    }


    fun addDebugData(){
        mAuthorIntroHolder.mStruct = AuthorIntroAdapter.AuthorStruct(FollowingFragment.DEBUG_URI2).apply{
            this.authorName = "t1"
            this.imgUris.add(FollowingFragment.DEBUG_URI2)
            this.imgUris.add(FollowingFragment.DEBUG_URI)
        }
        mAuthorIntroHolder.notifyDataChanged()

        for (i in 0 until 5) {
            mRelativeAdapter.addItem(ImageCardAdapter.CardStruct(Uri.parse("android.resource://com.eps3rd.pixiv/" + R.mipmap.test)))
        }

    }

    override fun getFragmentTitle(): Int {
        return R.string.fragment_work_detail
    }

}