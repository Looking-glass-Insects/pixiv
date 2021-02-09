package com.eps3rd.pixiv.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class BaseHeaderAdapter<Header : RecyclerView.ViewHolder, VH : RecyclerView.ViewHolder>:
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ITEM_HEAD = 0
        private const val ITEM_NORMAL = 1
    }

    open lateinit var normalAdapter: RecyclerView.Adapter<VH>
    open lateinit var mHeaderPresenter: HeaderAdapter<Header>

    override fun getItemViewType(position: Int): Int {
        return if (position < mHeaderPresenter.headerSize()) {
            ITEM_HEAD
        } else ITEM_NORMAL
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == ITEM_NORMAL) {
            val index = position - mHeaderPresenter.headerSize()
            normalAdapter.onBindViewHolder(holder as VH, index)
        } else if (viewType == ITEM_HEAD) {
            mHeaderPresenter.onBindHeader(holder as Header, position)
        }
    }

    override fun getItemCount(): Int {
        return normalAdapter.itemCount + mHeaderPresenter.headerSize()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_NORMAL) {
            normalAdapter.onCreateViewHolder(parent, viewType)
        } else {
            mHeaderPresenter.onCreateHeader(parent)
        }
    }

    interface HeaderAdapter<VH : RecyclerView.ViewHolder> {
        fun headerSize(): Int
        fun onCreateHeader(parent: ViewGroup): VH
        fun onBindHeader(holder: VH, position: Int)
    }
}