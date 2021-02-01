package com.eps3rd.app

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*
import kotlin.collections.ArrayList

class ExpandListAdapter : BaseDragAdapter<ExpandListAdapter.VH, ExpandListAdapter.ItemStruct>() {
    companion object {
        const val TAG = "ExpandListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_expand_list, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.mTitleView.text = mItems[position].getTitle()
        mItems[position].getExpandView()?.let {
            holder.mContainer.addView(it)
        }
        mItems[position].getSwitchListener()?.let {
            holder.addSwitchAction(it)
        }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTitleView: TextView = itemView.findViewById(R.id.tv_expand_title)
        var mContainer: ViewGroup = itemView.findViewById(R.id.container_expand_list)
        var mSwitch: SwitchMaterial = itemView.findViewById(R.id.switch_expand_item)
        private var mExpanded: Boolean = false
            set(value) {
                field = value
                Log.d(TAG, "set mExpanded:$mExpanded")
                if (mExpanded) {
                    mContainer
                        .animate()
                        .alpha(1f)
                        .withEndAction { mContainer.visibility = View.VISIBLE }
                        .start()
                } else {
                    mContainer
                        .animate()
                        .alpha(0f)
                        .withEndAction { mContainer.visibility = View.GONE }
                        .start()
                }
            }

        var mEnableSwitch = false
            set(value) {
                field = value
                mSwitch.visibility = if (mEnableSwitch) View.VISIBLE else View.GONE
            }

        fun addSwitchAction(listener: CompoundButton.OnCheckedChangeListener?) {
            if (listener != null) {
                mEnableSwitch = true
            }
            mSwitch.setOnCheckedChangeListener(listener)
        }

        init {
            itemView.setOnClickListener {
                Log.d(TAG, "click")
                mExpanded = !mExpanded
            }
        }
    }

    interface ItemStruct : BaseItem{
        fun getTitle(): String
        override fun getItemTag(): String
        fun getExpandView(): View?
        fun getSwitchListener(): CompoundButton.OnCheckedChangeListener? {
            return null
        }
    }
}