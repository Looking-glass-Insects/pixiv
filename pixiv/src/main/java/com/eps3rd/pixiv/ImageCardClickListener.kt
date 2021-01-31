package com.eps3rd.pixiv

import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.eps3rd.baselibrary.Constants

class ImageCardClickListener: View.OnClickListener {
    override fun onClick(v: View?) {
        ARouter.getInstance().build(Constants.MAIN_ACTIVITY)
            .withCharSequence(Constants.MAIN_ACTIVITY_REQUEST, com.eps3rd.pixiv.Constants.FRAGMENT_PATH_WORK_DETAIL)
            .navigation()
    }
}