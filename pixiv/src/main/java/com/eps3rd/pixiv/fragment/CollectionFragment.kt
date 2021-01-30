package com.eps3rd.pixiv.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.eps3rd.pixiv.Constants
import com.eps3rd.app.R
import com.eps3rd.pixiv.ImageCardAdapter


@Route(path = Constants.FRAGMENT_PATH_COLLECTION)
class CollectionFragment : Fragment() {


    private lateinit var mCollectionRV: RecyclerView
    private val mCollectionAdapter = ImageCardAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection, container, false)
        mCollectionRV = view.findViewById(R.id.rv_collection)
        mCollectionRV.layoutManager = GridLayoutManager(context,2)
        mCollectionAdapter.mShowAuthor = false
        mCollectionRV.adapter = mCollectionAdapter

        for (i in 0 until 5){
            mCollectionAdapter.addItem(ImageCardAdapter.CardStruct(Uri.parse("android.resource://com.eps3rd.pixiv/"+ R.mipmap.test)))
        }

        return view
    }

}