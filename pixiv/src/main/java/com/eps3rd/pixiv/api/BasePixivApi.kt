package com.eps3rd.pixiv.api

import android.util.Log
import com.eps3rd.pixiv.models.UserModel
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * acknowledge:
 * https://github.com/upbit/pixivpy
 * https://github.com/CeuiLiSA/Pixiv-Shaft
 */

class BasePixivApi {

    companion object {
        const val TAG = "BasePixivApi"
        const val CLIENT_ID = "MOBrBDS8blbauoSck0ZfDbtuzpyT"
        const val CLIENT_SECRET = "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj"
        const val HASH_SECRET = "28c1fdd170a5204386cb1313c7077b34f83e4aaf4aa829ce78c231e05b0bae2c"
        const val API_BASE_URL = "https://app-api.pixiv.net/"
        const val ACCOUNT_BASE_URL = "https://oauth.secure.pixiv.net/"
    }

    lateinit var accountApi: AccountApi
    lateinit var appApi: AppApi

    init {
        appApi = buildRetrofit(API_BASE_URL)!!.create(AppApi::class.java)
        accountApi = buildRetrofit(ACCOUNT_BASE_URL)!!.create(AccountApi::class.java)
    }


    suspend fun invokeAuth(name: String, pass: String, refreshToken: String? = null) =
        withContext(Dispatchers.Default) {
            val call = accountApi.login(
                CLIENT_ID,
                CLIENT_SECRET,
                "pixiv",
                true,
                "password",
                true,
                pass,
                name
            )
            try {
                return@withContext call.execute()
            } catch (e: Exception) {
                Log.d(TAG, "error:${Log.getStackTraceString(e)}")
                return@withContext null
            }
        }


    suspend fun getRecmdIllust(token: String) =
        withContext(Dispatchers.Default) {
            try {
                val call = appApi.getRecmdIllust(token)
                return@withContext call.execute()
            } catch (e: Exception) {
                Log.d(TAG, "error:${Log.getStackTraceString(e)}")
                return@withContext null
            }
        }


    suspend fun getNextIllust(token: String, nextUrl: String) =
        withContext(Dispatchers.Default) {
            try {
                val call = appApi.getNextIllust(token, nextUrl)
                return@withContext call.execute()
            } catch (e: Exception) {
                Log.d(TAG, "error:${Log.getStackTraceString(e)}")
                return@withContext null
            }
        }


    private fun getLogClient(): OkHttpClient.Builder? {
        val loggingInterceptor = HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger { message: String? ->
                Log.d(
                    TAG,
                    message!!
                )
            }
        )
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            //.addInterceptor(loggingInterceptor)
            .protocols(listOf(Protocol.HTTP_1_1))
    }

    private fun addHeader(before: Request.Builder): Request.Builder? {
        val pixivHeaders = PixivHeaders()
        before.addHeader(
            "User-Agent",
            "PixivAndroidApp/5.0.175 (Android 6.0; PixivBot)"
        )
            .addHeader("accept-language", "zh-cn")
            .addHeader(":authority", "app-api.pixiv.net")
            .addHeader("x-client-time", pixivHeaders.XClientTime)
            .addHeader("x-client-hash", pixivHeaders.XClientHash)
        return before
    }

    private fun buildRetrofit(baseUrl: String): Retrofit? {
        val builder: OkHttpClient.Builder = getLogClient()!!
        try {
            builder.addInterceptor { chain: Interceptor.Chain ->
                chain.proceed(
                    addHeader(chain.request().newBuilder())!!.build()
                )
            }
            builder.addInterceptor(TokenInterceptor())
        } catch (e: Exception) {
            Log.d(TAG, Log.getStackTraceString(e))
        }
        val client = builder.build()!!
        val gson = GsonBuilder().setLenient().create()!!
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseUrl)
            .build()
    }

}