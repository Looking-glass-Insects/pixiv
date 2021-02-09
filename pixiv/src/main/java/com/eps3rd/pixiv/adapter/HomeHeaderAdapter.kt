package com.eps3rd.pixiv.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eps3rd.app.R

class HomeHeaderAdapter : BaseHeaderAdapter.HeaderAdapter<HomeHeaderAdapter.HeaderVH> {

    val mRankAdapter = ImageCardAdapter()

    override fun headerSize(): Int {
        return 1
    }

    override fun onCreateHeader(parent: ViewGroup): HeaderVH {
        return HeaderVH(
            LayoutInflater.from(parent.context).inflate(R.layout.header_home, parent, false)
        )
    }

    override fun onBindHeader(holder: HeaderVH, position: Int) {
        holder.mRankRv.layoutManager =
            LinearLayoutManager(holder.mRankRv.context, RecyclerView.HORIZONTAL, false)
        holder.mRankRv.adapter = mRankAdapter
    }

    class HeaderVH(rootView: View) : RecyclerView.ViewHolder(rootView) {
        var mRankRv: RecyclerView = rootView.findViewById(R.id.rv_rank)
    }
}