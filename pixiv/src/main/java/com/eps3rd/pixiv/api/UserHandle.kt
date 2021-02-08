package com.eps3rd.pixiv.api

import android.util.Log
import com.eps3rd.pixiv.models.UserModel
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object UserHandle {

    private const val TAG = "UserHandle"
    private const val USER_HANDLE = "USER_HANDLE"

    var userModel: UserModel? = null
        set(value) {
            field = value
            GlobalScope.launch(Dispatchers.IO) {
                val userString = gson.toJson(userModel, UserModel::class.java)
                kv!!.encode(USER_HANDLE, userString)
            }
        }
    var gson = Gson()
    var kv: MMKV? = null
        set(value) {
            field = value
            GlobalScope.launch(Dispatchers.IO) {
                if (kv != null && kv!!.containsKey(USER_HANDLE)) {
                    val s = kv!!.decodeString(USER_HANDLE)
                    s?.let {
                        userModel = gson.fromJson(s, UserModel::class.java)
                        Log.d(TAG,"set user model by cache")
                    }
                }
            }
        }
    var api: BasePixivApi = BasePixivApi()
}