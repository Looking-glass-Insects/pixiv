package com.eps3rd.app

import android.app.Application
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.tencent.mmkv.MMKV




class PixivApp : Application() {

    companion object{
        private const val TAG = "MainApplication"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "isDebug:${isDebug()}")
        if (isDebug()) {
            ARouter.openLog()
            ARouter.openDebug()
        }

        val rootDir = MMKV.initialize(this)
        Log.d(TAG, "mmkv root: $rootDir")
        ARouter.init(this)
    }

    private fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }
}