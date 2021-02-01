package com.eps3rd.pixiv

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.eps3rd.baselibrary.Constants

class ImageCardClickListener: View.OnClickListener {
    override fun onClick(v: View?) {
        val b = Bundle()
        b.putString(Constants.MAIN_ACTIVITY_START_FRAGMENT, com.eps3rd.pixiv.Constants.FRAGMENT_PATH_WORK_DETAIL)
        ARouter.getInstance().build(Constants.MAIN_ACTIVITY)
            .withBundle(Constants.MAIN_ACTIVITY_REQUEST,b)
            .navigation()
    }
}