package com.eps3rd.app.transaction

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.eps3rd.app.R
import com.eps3rd.pixiv.GlideApp
import com.eps3rd.pixiv.transform.GlideCircleBorderTransform
import com.eps3rd.pixiv.api.UserHandle
import com.eps3rd.pixiv.models.UserModel
import com.eps3rd.pixiv.util.GlideUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response

class UserHeadPresenter(rootView: View) {

    companion object {
        const val TAG = "UserHeadPresenter"
    }


    private lateinit var mContext: Context
    private lateinit var mDrawerHeaderContainer: ViewGroup
    private lateinit var mUserImage: ImageView
    private lateinit var mDrawerHeader: ImageView
    private lateinit var mTvUserName: TextView

    init {
        mContext = rootView.context
        mUserImage = rootView.findViewById(R.id.drawer_user_img)
        mDrawerHeaderContainer = rootView.findViewById<ViewGroup>(R.id.drawer_header_container)
        mDrawerHeader = rootView.findViewById(R.id.drawer_header)
        mTvUserName = rootView.findViewById(R.id.tv_user_name)

        mDrawerHeaderContainer.setOnClickListener {

            //start to sign in

            val builder = AlertDialog.Builder(mContext)
            val view = LayoutInflater.from(mContext).inflate(R.layout.fragment_sign_in, null)
            val userEt = view.findViewById<EditText>(R.id.et_user_name)
            val passEt = view.findViewById<EditText>(R.id.et_password)

            UserHandle.userModel?.response?.user?.let {
                userEt.setText(it.name)
            }

            builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.signin,
                    DialogInterface.OnClickListener { dialog, id ->
                        // sign in the user ...
                        val user = userEt.text.toString()
                        val pass = passEt.text.toString()
                        Log.d(TAG, "sign in:$user,$pass")
                        GlobalScope.launch(Dispatchers.Default) {
                            try {
                                val response: Response<UserModel>? =
                                    UserHandle.api.invokeAuth("503664974", "hy1234567000")
                                if (response == null) {
                                    Log.d(TAG, "response is null")
                                    return@launch
                                }
                                when {
                                    response.code() !in arrayOf(200, 301, 302) -> {
                                        Log.d(
                                            TAG,
                                            "fail:${response.code()},${response.errorBody()
                                                ?.string()},${response.body()}"
                                        )
                                    }
                                    response.code() == 400 -> {
                                        // try to refresh Token
                                        Log.d(TAG, "fail 400:${response.errorBody()?.string()}")
                                    }
                                    else -> {
                                        UserHandle.userModel = response.body()
                                        UserHandle.userModel?.getResponse()?.getUser()
                                            ?.setPassword(pass)
                                        UserHandle.userModel?.getResponse()?.getUser()
                                            ?.setIs_login(true)

                                        setUp()
                                        Log.d(
                                            TAG,
                                            "success ${response.code()} login:${UserHandle.userModel?.response?.user}"
                                        )
                                    }
                                }
                            } catch (e: java.lang.Exception) {
                                Log.d(TAG, Log.getStackTraceString(e))
                            }
                        }
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
            builder.create().show()
        }
    }

    fun setUp() {
        loadUserImage()
        loadUserName()
    }

    private fun loadUserName() {
        UserHandle.userModel?.response?.user?.let {
            mTvUserName.text = it.name
        }
    }

    private fun loadUserImage() {
        GlideApp.with(mContext)
            .load(GlideUtil.getHead(UserHandle.userModel?.response?.user))
            .placeholder(R.drawable.ic_round_image_search_24)
            .error(R.drawable.bg_drawer_item)
            .transform(
                FitCenter(),
                GlideCircleBorderTransform(
                    4,
                    mContext.resources.getColor(R.color.color_primary)
                )
            ).into(mUserImage)
    }

}