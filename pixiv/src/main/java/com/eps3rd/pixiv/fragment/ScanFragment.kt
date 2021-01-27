package com.eps3rd.pixiv.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.eps3rd.baselibrary.Constants
import com.eps3rd.pixiv.ImageCardAdapter
import com.eps3rd.app.R


@Route(path = Constants.FRAGMENT_PATH_SCAN)
class ScanFragment : Fragment() {

    private var mRecyclerView: RecyclerView? = null
    private val mImagesAdapter = ImageCardAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_imgs, container, false)
        mRecyclerView = view.findViewById(R.id.items_img)
        mRecyclerView?.layoutManager = GridLayoutManager(context,2)
        mRecyclerView?.adapter = mImagesAdapter
        return view
    }

    override fun onResume() {
        super.onResume()
        mImagesAdapter.addItem(ImageCardAdapter.ImageStruct(Uri.parse("android.resource://com.eps3rd.pixiv/" + R.mipmap.ic_launcher_round)))
    }

}