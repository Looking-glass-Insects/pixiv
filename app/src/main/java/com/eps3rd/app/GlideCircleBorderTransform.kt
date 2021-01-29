package com.eps3rd.app

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest


class GlideCircleBorderTransform(borderWidth:Int, @ColorInt color: Int) : CircleCrop() {
    private val mBorder = borderWidth
    private var mBorderPaint : Paint = Paint()
    private val mColor = color

    init {
        mBorderPaint.color = color
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        mBorderPaint.strokeWidth = mBorder.toFloat()
        mBorderPaint.isDither = true
    }



    override fun hashCode(): Int {
        return Util.hashCode(
            ID.hashCode(),
            mBorder * 31 + mColor
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other is GlideCircleBorderTransform) {
            return mBorder == other.mBorder && mColor == other.mColor
        }
        return false
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID.toByteArray(CHARSET))

        val degreesData =
            ByteBuffer.allocate(4).putInt(mBorder * 31 + mColor).array()
        messageDigest.update(degreesData)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val out = super.transform(pool, toTransform, outWidth, outHeight)
        val radius = outWidth / 2f
        val borderRadius: Float = radius - mBorder / 2
        val canvas = Canvas(out)
        canvas.drawCircle(radius, radius, borderRadius, mBorderPaint)
        return out
    }

    companion object {
        private const val ID : String = "GlideCircleBorderTransform"
    }
}