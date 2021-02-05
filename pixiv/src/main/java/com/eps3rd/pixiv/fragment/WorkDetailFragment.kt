package com.eps3rd.pixiv.fragment

import android.content.res.ColorStateList
import android.graphics.Color
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
import android.widget.Toast
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.eps3rd.pixiv.Constants
import com.eps3rd.app.R
import com.eps3rd.pixiv.AuthorIntroAdapter
import com.eps3rd.pixiv.IFragment
import com.eps3rd.pixiv.ImageCardAdapter
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton


@Route(path = Constants.FRAGMENT_PATH_WORK_DETAIL)
class WorkDetailFragment : Fragment(), IFragment {

    companion object{
       const val TAG = "WorkDetailFragment"
    }

    private lateinit var mImage: ImageView
    private lateinit var mTvTime: TextView
    private lateinit var mTvViews: TextView
    private lateinit var mTvLikes: TextView
    private lateinit var mTagsContainer:ChipGroup
    private lateinit var mAuthorIntroHolder: AuthorIntroAdapter.AuthorVH
    private lateinit var mRvRelatives: RecyclerView
    private lateinit var mFloatingButton: FloatingActionButton


    private var mCollected = false
        set(value){
            field = value
            if (mCollected){
                mFloatingButton.setImageResource(R.drawable.ic_round_favorite_24)
            }
            else{
                mFloatingButton.setImageResource(R.drawable.ic_round_favorite_border_24)
            }
        }

    private val mRelativeAdapter: ImageCardAdapter = ImageCardAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        mFloatingButton = view.findViewById(R.id.btn_collection)
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

//        ImageViewCompat.setImageTintList(
//            mFloatingButton,
//            ColorStateList.valueOf(Color.WHITE)
//        )

        mFloatingButton.setOnClickListener {
            mCollected = !mCollected
            Toast.makeText(context,"hello",Toast.LENGTH_SHORT).show()
        }

        // DEBUG CODE
        addDebugData()
        mTagsContainer.addView(CollectionFragment.buildTag(inflater, "test1"))
        mTagsContainer.addView(CollectionFragment.buildTag(inflater, "test2"))
        return view
    }

    override fun onResume() {
        super.onResume()
    }


    private fun addDebugData(){
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