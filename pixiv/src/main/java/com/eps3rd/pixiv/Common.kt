package com.eps3rd.pixiv

import okhttp3.Response
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException

class Common {
    companion object {
        fun getResponseBody(response: Response): String? {
            val UTF8 = Charset.forName("UTF-8")
            val responseBody = response.body()
            val source = responseBody!!.source()
            try {
                source.request(Long.MAX_VALUE) // Buffer the entire body.
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val buffer = source.buffer()
            var charset = UTF8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8)
                } catch (e: UnsupportedCharsetException) {
                    e.printStackTrace()
                }
            }
            return buffer.clone().readString(charset)
        }
    }
}