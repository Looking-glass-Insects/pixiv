package com.eps3rd.pixiv

import android.app.Application
import android.content.Context
import android.os.Looper
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat


@GlideModule
class GlideSettingsModule : AppGlideModule() {

    companion object {
        const val DEFAULT_MEM_CACHE = 1024 * 1024 * 20 // 20mb
        const val DEFAULT_DISK_CACHE = 1024 * 1024 * 2000
        const val CACHE_NAME = "GlideCache"

        suspend fun getCacheSize(context: Application) = withContext(Dispatchers.IO) {
            return@withContext try {
                val size = getFolderSize(
                    File(
                        context.getCacheDir()
                            .toString() + File.separator + CACHE_NAME
                    )
                )
                DecimalFormat("0.##").let {
                    it.roundingMode = RoundingMode.FLOOR
                    "${it.format(size / (1024.0f * 1024))} M"
                }
            } catch (e: Exception) {
                "N/A"
            }
        }

        suspend fun clearCacheDiskSelf(context: Application) =  withContext(Dispatchers.IO) {
            return@withContext try {
                Glide.get(context).clearDiskCache()
                true
            } catch (e: java.lang.Exception) {
                false
            }
        }

        fun getFolderSize(file: File): Long {
            var size = 0L
            try {
                val fileList = file.listFiles()
                for (file in fileList) {
                    size += if (file.isDirectory) getFolderSize(file)
                    else file.length();
                }
            } catch (e: Exception) {
                size = 0L
            }
            return size
        }
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setMemoryCache(LruResourceCache(DEFAULT_MEM_CACHE.toLong()))
        builder.setDiskCache(
            InternalCacheDiskCacheFactory(
                context,
                CACHE_NAME,
                DEFAULT_DISK_CACHE.toLong()
            )
        )
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}