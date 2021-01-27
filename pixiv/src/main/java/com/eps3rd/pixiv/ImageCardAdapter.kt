package com.eps3rd.pixiv

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.eps3rd.app.R


class ImageCardAdapter : RecyclerView.Adapter<ImageCardAdapter.ImageCardVH>() {

    companion object {
        const val TAG = "ImageCardAdapter"
    }

    private val mImageItems = ArrayList<ImageStruct>()
    private val mCollectionCallback =
        CollectionCallbackImpl()

    fun addItem(item: ImageStruct) {
        mImageItems.add(item)
        notifyItemInserted(mImageItems.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageCardVH {
        return ImageCardVH(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.item_img, parent, false)
        )
    }


    override fun getItemCount(): Int {
        return mImageItems.size
    }

    override fun onBindViewHolder(holder: ImageCardVH, position: Int) {
        val item = mImageItems[position]
        holder.mCollectionCallback = mCollectionCallback
        holder.setImage(item.uri)
    }


    @SuppressLint("ClickableViewAccessibility")
    class ImageCardVH(rootView: View) : RecyclerView.ViewHolder(rootView) {

        companion object {
            const val DEFAULT_SCALE = 0.8f
        }

        var mImageView: ImageView? = null
        var mCollectionButton: ImageView? = null
        var mCollectionCallback: CollectionCallback? = null
        var mUri: Uri? = null
        var mCollected: Boolean = false

        init {
            mImageView = rootView.findViewById(R.id.iv_img)
            mCollectionButton = rootView.findViewById(R.id.iv_collection)
            mCollectionButton?.setOnTouchListener { v, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        mCollectionButton?.scaleY =
                            DEFAULT_SCALE
                        mCollectionButton?.scaleX =
                            DEFAULT_SCALE
                        return@setOnTouchListener true
                    }
                    MotionEvent.ACTION_UP -> {
                        mCollectionButton?.animate()!!.
                        scaleX(1.0f).
                        scaleY(1.0f).
                        setInterpolator(AccelerateInterpolator()).
                        start()
                        mCollected = mCollectionCallback?.isCollected(mUri) ?: false
                        mCollected = !mCollected
                        mCollectionCallback?.setCollected(mUri, mCollected)

                        Glide.with(mCollectionButton!!)
                            .load(if (mCollected) R.drawable.ic_round_favorite_24 else R.drawable.ic_round_favorite_border_24)
                            .into(mCollectionButton!!)
                        return@setOnTouchListener true
                    }
                    MotionEvent.ACTION_CANCEL->{
                        mCollectionButton?.animate()!!.
                        scaleX(1.0f).
                        scaleY(1.0f).
                        setInterpolator(AccelerateInterpolator()).
                        start()
                    }
                }
                return@setOnTouchListener false
            }
        }

        fun setImage(uri: Uri) {
            this.mUri = uri
            mCollected = this.mCollectionCallback?.isCollected(uri) ?: false
            if (mImageView == null)
                return
            Glide.with(mImageView!!)
                .load(uri)
                .placeholder(R.drawable.ic_launcher_background)
                .transform(FitCenter())
                .into(mImageView!!)
        }
    }

    interface CollectionCallback {
        fun isCollected(uri: Uri?): Boolean
        fun setCollected(uri: Uri?, collected: Boolean)
    }

    class CollectionCallbackImpl :
        CollectionCallback {

        var collected = false

        override fun isCollected(uri: Uri?): Boolean {
            Log.d(TAG, "isCollected uri:$uri")
            return collected
        }

        override fun setCollected(uri: Uri?, collected: Boolean) {
            Log.d(TAG, "setCollected uri:$uri")
            this.collected = collected
        }

    }

    data class ImageStruct(
        val uri: Uri
    )

}