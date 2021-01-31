package com.eps3rd.pixiv.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.eps3rd.pixiv.Constants
import com.eps3rd.app.R


@Route(path = Constants.FRAGMENT_PATH_AUTHOR)
class AuthorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_author, container, false)

        return view
    }

}