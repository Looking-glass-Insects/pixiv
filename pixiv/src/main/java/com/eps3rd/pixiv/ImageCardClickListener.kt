package com.eps3rd.pixiv

import android.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.widget.ListPopupWindow
import androidx.appcompat.widget.ListPopupWindow.POSITION_PROMPT_ABOVE
import com.alibaba.android.arouter.launcher.ARouter
import com.eps3rd.baselibrary.Constants


class ImageCardClickListener : View.OnClickListener, View.OnLongClickListener {

    override fun onClick(v: View?) {
        val b = Bundle()
        b.putString(
            Constants.MAIN_ACTIVITY_START_FRAGMENT,
            com.eps3rd.pixiv.Constants.FRAGMENT_PATH_WORK_DETAIL
        )
        ARouter.getInstance().build(Constants.MAIN_ACTIVITY)
            .withBundle(Constants.MAIN_ACTIVITY_REQUEST, b)
            .navigation()
    }

    override fun onLongClick(v: View?): Boolean {
        val mPopupWindow = ListPopupWindow(v!!.context!!)
        mPopupWindow.setAdapter(
            ArrayAdapter<String>(
                v!!.context!!,
                R.layout.simple_list_item_1,
                arrayOf("hello", "word")
            )
        )
        mPopupWindow.width = ListPopupWindow.WRAP_CONTENT
        mPopupWindow.height = ListPopupWindow.WRAP_CONTENT
        mPopupWindow.anchorView = v //设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
        mPopupWindow.isModal = true //设置是否是模式
        mPopupWindow.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            mPopupWindow.dismiss()
        })
        mPopupWindow.show()
        return true
    }
}