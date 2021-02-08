package com.eps3rd.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eps3rd.app.R

class ControlItemAdapter :
    BaseDragAdapter<ControlItemAdapter.VH, ControlItemAdapter.ControlItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.item_control, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.setText(mItems[position].title)
    }

    data class ControlItem(val title: String) : BaseItem {
        var fragmentPath: String? = null
        var tag: String = ""

        override fun getItemTag(): String {
            return tag
        }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTextView = itemView.findViewById<TextView>(R.id.control_item)

        fun setText(text: String) {
            mTextView.text = text
        }
    }
}