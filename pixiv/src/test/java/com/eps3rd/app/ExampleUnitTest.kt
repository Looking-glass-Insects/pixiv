package com.eps3rd.app

import com.eps3rd.pixiv.models.UserModel
import com.google.gson.Gson
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.junit.Test
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    companion object{
        val TAG = "ExampleUnitTest"
    }

    @Test
    fun addition_isCorrect() {
//        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'")//'%Y-%m-%dT%H:%M:%S+00:00''2021-02-07T04:59:40+00:00'
//        val date = Date()
//
//        df.setTimeZone(TimeZone.getTimeZone("GMT"));
//        //print("${df.format(date.time)}")
//
//
//        val hash = "28c1fdd170a5204386cb1313c7077b34f83e4aaf4aa829ce78c231e05b0bae2c"
//        val password = "ILoveJava"
//
//        val md: MessageDigest = MessageDigest.getInstance("MD5")
//
//        val s :String= "2021-02-07T05:11:02+00:0028c1fdd170a5204386cb1313c7077b34f83e4aaf4aa829ce78c231e05b0bae2c"
//
//        val thedigest = md.digest(s.toByteArray())
//
//        val encodedBytes: ByteArray = Base64.encodeBase64(thedigest)
//        print(DigestUtils.md5Hex(s))

        val s = "{\"access_token\":\"gcAHlQMv3ScINRUNG2KjGsYA7zDcGEzHDM-w6Q2GXfs\",\"expires_in\":3600,\"token_type\":\"bearer\",\"scope\":\"\",\"refresh_token\":\"8H7mH_lEphfud-_nhfRbmXCPxJGpTmhknXns75b_90Q\",\"user\":{\"profile_image_urls\":{\"px_16x16\":\"https:\\/\\/i.pximg.net\\/user-profile\\/img\\/2020\\/09\\/10\\/01\\/46\\/59\\/19333893_ab7e72430e53ba3784504487d9769292_16.jpg\",\"px_50x50\":\"https:\\/\\/i.pximg.net\\/user-profile\\/img\\/2020\\/09\\/10\\/01\\/46\\/59\\/19333893_ab7e72430e53ba3784504487d9769292_50.jpg\",\"px_170x170\":\"https:\\/\\/i.pximg.net\\/user-profile\\/img\\/2020\\/09\\/10\\/01\\/46\\/59\\/19333893_ab7e72430e53ba3784504487d9769292_170.jpg\"},\"id\":\"19790053\",\"name\":\"eps3rd\",\"account\":\"503664974\",\"mail_address\":\"503664974@qq.com\",\"is_premium\":false,\"x_restrict\":2,\"is_mail_authorized\":true,\"require_policy_agreement\":false},\"device_token\":\"64f77b97c72af31077555f12e1020f31\",\"response\":{\"access_token\":\"gcAHlQMv3ScINRUNG2KjGsYA7zDcGEzHDM-w6Q2GXfs\",\"expires_in\":3600,\"token_type\":\"bearer\",\"scope\":\"\",\"refresh_token\":\"8H7mH_lEphfud-_nhfRbmXCPxJGpTmhknXns75b_90Q\",\"user\":{\"profile_image_urls\":{\"px_16x16\":\"https:\\/\\/i.pximg.net\\/user-profile\\/img\\/2020\\/09\\/10\\/01\\/46\\/59\\/19333893_ab7e72430e53ba3784504487d9769292_16.jpg\",\"px_50x50\":\"https:\\/\\/i.pximg.net\\/user-profile\\/img\\/2020\\/09\\/10\\/01\\/46\\/59\\/19333893_ab7e72430e53ba3784504487d9769292_50.jpg\",\"px_170x170\":\"https:\\/\\/i.pximg.net\\/user-profile\\/img\\/2020\\/09\\/10\\/01\\/46\\/59\\/19333893_ab7e72430e53ba3784504487d9769292_170.jpg\"},\"id\":\"19790053\",\"name\":\"eps3rd\",\"account\":\"503664974\",\"mail_address\":\"503664974@qq.com\",\"is_premium\":false,\"x_restrict\":2,\"is_mail_authorized\":true,\"require_policy_agreement\":false},\"device_token\":\"64f77b97c72af31077555f12e1020f31\"}}"
        val gson = Gson()
        val b = gson.fromJson(s,UserModel.ResponseBean::class.java)
        print(b.user)
    }
}