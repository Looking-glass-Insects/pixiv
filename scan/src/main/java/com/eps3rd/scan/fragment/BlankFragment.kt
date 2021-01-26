package com.eps3rd.scan.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.eps3rd.baselibrary.Constants
import com.eps3rd.scan.R


@Route(path = Constants.FRAGMENT_PATH_BLANK)
class BlankFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var mIvTest : ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        arguments?.let {
            param1 = it.getString("param1")
            param2 = it.getString("param2")
        }
        Log.d("BlankFragment","param1:$param1,param2:$param2")
        val view = inflater.inflate(R.layout.fragment_blank, container, false)
        mIvTest = view?.findViewById<ImageView>(R.id.iv_test)
        mIvTest?.let {
            Glide.with(this)
                .load(R.mipmap.ic_launcher_round)
                .into(it)
        }
        return view
    }

}