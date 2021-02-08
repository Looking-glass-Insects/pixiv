package com.eps3rd.pixiv

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.ListPopupWindow
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.eps3rd.app.R
import com.eps3rd.baselibrary.Constants
import com.eps3rd.baselibrary.FileHelper
import kotlinx.coroutines.*
import java.io.File
import java.lang.Exception


class ImageCardClickListener : View.OnClickListener, View.OnLongClickListener {

    companion object {
        const val TAG = "ImageCardClickListener"
    }

    override fun onClick(v: View?) {
        Log.d(TAG, "onClick")
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
        return false
        val uri = v?.tag as Uri
        val context = v!!.context!!
        val mPopupWindow = ListPopupWindow(v!!.context!!)
        mPopupWindow.setAdapter(
            ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                arrayOf(context.resources.getString(R.string.save_img))
            )
        )
        mPopupWindow.width = ListPopupWindow.WRAP_CONTENT
        mPopupWindow.height = ListPopupWindow.WRAP_CONTENT
        mPopupWindow.anchorView = v //设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
        mPopupWindow.isModal = true //设置是否是模式
        mPopupWindow.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            Glide.with(context).downloadOnly().load(uri).addListener(object :
                RequestListener<File> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<File>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onResourceReady(
                    resource: File?,
                    model: Any?,
                    target: Target<File>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "saving $uri")
                    GlobalScope.launch(Dispatchers.Main) {
                        try {
                            FileHelper.writeFileToGallery(
                                context,
                                resource!!,
                                "${uri}.jpg",
                                "image/jpg"
                            )
                            Toast.makeText(context, R.string.toast_success, Toast.LENGTH_SHORT)
                                .show()
                        } catch (e: Exception) {
                            Toast.makeText(context, R.string.toast_fail, Toast.LENGTH_SHORT).show()
                        }
                    }
                    return false
                }

            }).preload()
            mPopupWindow.dismiss()
        })
        mPopupWindow.show()
        return true
    }
}