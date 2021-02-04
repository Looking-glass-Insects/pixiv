package com.eps3rd.baselibrary

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object FileHelper {

    private const val APP_FOLDER_NAME = "eps3rd"


    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun writeFileToGallery(
        context: Context,
        file: File,
        displayName: String,
        mimeType: String
    )  =  withContext(Dispatchers.IO){
        val cacheFileStream = FileInputStream(file)
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)

        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$APP_FOLDER_NAME/")
        val uri =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val outputStream = context.contentResolver.openOutputStream(uri!!)

        val buffer = ByteArray(1024)
        while (cacheFileStream.read(buffer) > 0) {
            outputStream!!.write(buffer)
        }

        cacheFileStream.close()
        outputStream!!.close()
    }

}