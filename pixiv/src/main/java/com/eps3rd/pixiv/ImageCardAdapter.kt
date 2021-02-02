package com.eps3rd.pixiv

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.eps3rd.app.R
import com.eps3rd.pixiv.ImageCardAdapter.ImageCardVH.Companion.NO_IMG_URI


class ImageCardAdapter : RecyclerView.Adapter<ImageCardAdapter.ImageCardVH>() {

    companion object {
        const val TAG = "ImageCardAdapter"
        const val DEFAULT_MAX_COUNT = 50
    }

    private val mImageItems = ArrayList<CardStruct>()
    private val mCollectionCallback =
        CollectionCallbackImpl()

    var mShowAuthor: Boolean = true
    var mShowOverlay: Boolean = true
    var mShowCollection: Boolean = true
    var mMaxCount = DEFAULT_MAX_COUNT
    val mClickListener = ImageCardClickListener()


    fun addItem(item: CardStruct) {
        if (mImageItems.size >= mMaxCount)
            return
        mImageItems.add(item)
        notifyItemInserted(mImageItems.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageCardVH {
        val vh = ImageCardVH(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.item_img, parent, false)
        )
        vh.setListener(mClickListener)
        return vh
    }


    override fun getItemCount(): Int {
        return mImageItems.size
    }

    override fun onBindViewHolder(holder: ImageCardVH, position: Int) {
        val item = mImageItems[position]
        holder.mCollectionCallback = mCollectionCallback
        holder.setImage(item.imgUri)
        holder.mShowAuthor = mShowAuthor
        holder.mShowOverlay = mShowOverlay

        if (!mShowCollection)
            holder.mCollectionButton.visibility = View.GONE
    }


    @SuppressLint("ClickableViewAccessibility")
    class ImageCardVH(rootView: View) : RecyclerView.ViewHolder(rootView) {

        companion object {
            val NO_IMG_URI =
                Uri.parse("android.resource://com.eps3rd.pixiv/" + R.drawable.ic_round_image_24)
        }

        private var mRootView: View
        private var mImageView: ImageView
        private var mOverlay: ViewGroup
        private var mImageCount: TextView
        private var mImageTitle: TextView
        private var mAuthorName: TextView
        private var mAuthorIcon: ImageView
        var mCollectionButton: ImageView
        var mTag: Any? = null

        private val cardStruct: CardStruct = CardStruct(NO_IMG_URI)

        var mCollectionCallback: CollectionCallback? = null
        var mCollected: Boolean = false
        var mShowAuthor: Boolean = true
            set(value) {
                field = value
                loadAuthor()
                loadTitle()
            }
        var mShowOverlay: Boolean = true
            set(value) {
                field = value
                loadOverlay()
            }

        init {
            mRootView = rootView
            mImageView = rootView.findViewById(R.id.iv_img)
            mCollectionButton = rootView.findViewById(R.id.iv_collection)
            mOverlay = rootView.findViewById(R.id.container_overlay)
            mImageCount = rootView.findViewById(R.id.img_count)
            mImageTitle = rootView.findViewById(R.id.image_title)
            mAuthorName = rootView.findViewById(R.id.author_name)
            mAuthorIcon = rootView.findViewById(R.id.author_small_icon)

            val listener = ImageCardClickListener()


            mCollectionButton.setOnClickListener {
                mCollected = mCollectionCallback?.isCollected(cardStruct.imgUri) ?: false
                mCollected = !mCollected
                mCollectionCallback?.setCollected(cardStruct.imgUri, mCollected)
                Glide.with(mCollectionButton)
                    .load(if (mCollected) R.drawable.ic_round_favorite_24 else R.drawable.ic_round_favorite_border_24)
                    .into(mCollectionButton)
            }

            loadAuthor()
            loadTitle()
            loadOverlay()
        }

        private fun loadAuthor() {
            if (!mShowAuthor) {
                mAuthorIcon.visibility = View.GONE
                mAuthorName.visibility = View.GONE
                return
            }

            Glide.with(mImageView)
                .load(cardStruct.authorIcon)
                .placeholder(R.drawable.ic_round_image_search_24)
                .error(R.drawable.ic_round_broken_image_24)
                .transform(FitCenter(), CircleCrop())
                .into(mAuthorIcon)
            mAuthorName.text = cardStruct.authorName
        }

        private fun loadTitle() {
            if (!mShowAuthor) {
                mImageTitle.visibility = View.GONE
                return
            }
            mImageTitle.text = cardStruct.imageTitle
        }

        private fun loadOverlay() {
            if (!mShowOverlay) {
                mOverlay.visibility = View.GONE
            }
            mImageCount.text = cardStruct.imageCount
        }

        fun setImage(uri: Uri) {
            this.cardStruct.imgUri = uri

            mCollected = this.mCollectionCallback?.isCollected(uri) ?: false
            Glide.with(mCollectionButton)
                .load(if (mCollected) R.drawable.ic_round_favorite_24 else R.drawable.ic_round_favorite_border_24)
                .into(mCollectionButton)

            Glide.with(mImageView)
                .load(uri)
                .placeholder(R.drawable.ic_round_image_search_24)
                .error(R.drawable.ic_round_broken_image_24)
                .transform(CenterCrop())
                .into(mImageView)
        }

        fun setListener(listener: ImageCardClickListener) {
            mRootView.setOnLongClickListener(listener)
            mRootView.setOnClickListener(listener)
        }

        fun setAuthorAndTitle(iconUri: Uri, name: String, title: String) {
            cardStruct.authorIcon = iconUri
            cardStruct.authorName = name
            cardStruct.imageTitle = title
            loadAuthor()
            loadTitle()
        }

        fun setOverlayCount(count: String) {
            cardStruct.imageCount = count
            loadOverlay()
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

    data class CardStruct(
        var imgUri: Uri
    ) {
        var imageCount: String = "NaN"
        var imageTitle: String = "Untitled"
        var authorName: String = "N/A"
        var authorIcon: Uri = NO_IMG_URI
    }

}