package com.eps3rd.pixiv.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.eps3rd.pixiv.Constants
import com.eps3rd.app.R
import com.eps3rd.pixiv.adapter.AuthorIntroAdapter
import com.eps3rd.pixiv.IFragment
import com.google.android.material.chip.ChipGroup
import java.lang.IllegalStateException


@Route(path = Constants.FRAGMENT_PATH_FOLLOWING)
class FollowingFragment : Fragment(), IFragment {

    companion object{
        const val TAG = "FollowingFragment"
        val DEBUG_URI = Uri.parse("android.resource://com.eps3rd.pixiv/"+ R.mipmap.test)
        val DEBUG_URI2 = Uri.parse("android.resource://com.eps3rd.pixiv/"+ R.mipmap.test2)
    }


    private lateinit var mRvFollow: RecyclerView
    private val mPublicAdapter: AuthorIntroAdapter =
        AuthorIntroAdapter()
    private val mPrivateAdapter: AuthorIntroAdapter =
        AuthorIntroAdapter()
    private lateinit var mChipGroup: ChipGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_following, container, false)
        mRvFollow = view.findViewById(R.id.rv_following)
        mChipGroup = view.findViewById(R.id.chipGroup)
        mChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.chip_following_public ->{
                    mRvFollow.adapter = mPublicAdapter
                }
                R.id.chip_following_private -> {
                    mRvFollow.adapter = mPrivateAdapter
                }
                else ->{
                    throw IllegalStateException("error")
                }
            }
        }
        mRvFollow.layoutManager = LinearLayoutManager(context)
        mRvFollow.adapter = mPublicAdapter
        return view
    }


    override fun onResume() {
        super.onResume()
        // DEBUG CODE
        val items = ArrayList<AuthorIntroAdapter.AuthorStruct>()
        items.add(AuthorIntroAdapter.AuthorStruct(DEBUG_URI2).apply{
            this.authorName = "t1"
            this.imgUris.add(DEBUG_URI2)
            this.imgUris.add(DEBUG_URI)
        })
        mPrivateAdapter.addItems(items)
        items.add(AuthorIntroAdapter.AuthorStruct(DEBUG_URI).apply{
            this.authorName = "tttttttttttttttt2"
            this.imgUris.add(DEBUG_URI)
            this.imgUris.add(DEBUG_URI2)
            this.imgUris.add(DEBUG_URI)
        })
        mPublicAdapter.addItems(items)
        // DEBUG CODE
    }

    override fun getFragmentTitle(): Int {
        return R.string.fragment_following
    }

}