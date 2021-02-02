package com.eps3rd.app

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

abstract class BaseDragAdapter<VH: RecyclerView.ViewHolder,ItemType : BaseDragAdapter.BaseItem>: RecyclerView.Adapter<VH>() {

    protected val mItems : MutableList<ItemType> = ArrayList()

    val mTagList: MutableList<String> = ArrayList()

    open var mTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val from = viewHolder.adapterPosition
            val to = target.adapterPosition
            if (from < 0 || to < 0) return false
            if (to == from) {
                return true
            }
            Collections.swap(mTagList, from, to)
            notifyItemMoved(from, to)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }

    })

    open fun addItem(item: ItemType) {
        mItems.add(item)
        mTagList.add(item.getItemTag())
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    interface BaseItem{
        fun getItemTag():String
    }
}