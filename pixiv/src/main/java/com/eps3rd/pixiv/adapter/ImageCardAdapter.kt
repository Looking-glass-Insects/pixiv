package com.eps3rd.pixiv.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.eps3rd.app.R
import com.eps3rd.pixiv.GlideApp
import com.eps3rd.pixiv.ImageCardClickListener
import com.eps3rd.pixiv.models.IllustsBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ImageCardAdapter : RecyclerView.Adapter<ImageCardAdapter.ImageCardVH>() {

    companion object {
        const val TAG = "ImageCardAdapter"
        const val DEFAULT_MAX_COUNT = 10000
    }

    private val mImageItems = ArrayList<CardStruct>()
    private val mCollectionCallback =
        CollectionCallbackImpl()

    var mShowAuthor: Boolean = true
    var mShowOverlay: Boolean = true
    var mShowCollection: Boolean = true
    var mMaxCount =
        DEFAULT_MAX_COUNT
    val mClickListener = ImageCardClickListener()

    @Deprecated("optimize")
    fun addItem(item: CardStruct) {
        if (mImageItems.size >= mMaxCount) {
            Log.d(TAG,"max count ignore")
            return
        }
        mImageItems.add(item)
    }

    fun addItems(items: List<CardStruct>){
        if (mImageItems.size >= mMaxCount) {
            Log.d(TAG,"max count ignore")
            return
        }
        val start = mImageItems.size
        mImageItems.addAll(items)
    }

    fun clearItem(){
        if (mImageItems.size == 0)
            return
        mImageItems.clear()
        GlobalScope.launch(Dispatchers.Main){
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageCardVH {
//        Log.d(TAG,"onCreateViewHolder")
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
//        Log.d(TAG,"onBindViewHolder")
        holder.setTag(item.imageBean!!)
        holder.mCollectionCallback = mCollectionCallback
        holder.setImage(item.imgUri)
        holder.mShowAuthor = mShowAuthor
        holder.mShowOverlay = mShowOverlay
        holder.setListener(mClickListener)
        holder.setAuthorAndTitle(item.authorIcon,item.authorName,item.imageTitle)
        holder.setOverlayCount(item.imageCount)
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

        private var cardStruct: CardStruct =
            CardStruct()

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

            mCollectionButton.setOnClickListener {

                Log.d(TAG,"on collection click")
//                mCollectionCallback?.setCollected(cardStruct.imgUri, mCollected)
//                Glide.with(mCollectionButton)
//                    .load(if (mCollected) R.drawable.ic_round_favorite_24 else R.drawable.ic_round_favorite_border_24)
//                    .into(mCollectionButton)
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
                .transition(DrawableTransitionOptions.withCrossFade())
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
            mImageCount.text = cardStruct.imageCount + "P"
        }

        fun setImage(uri: GlideUrl?) {
            this.cardStruct.imgUri = uri
            GlideApp.with(mCollectionButton)
                .load(if (mCollected) R.drawable.ic_round_favorite_24 else R.drawable.ic_round_favorite_border_24)
                .into(mCollectionButton)

            GlideApp.with(mImageView)
                .load(uri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(CenterCrop())
                .into(mImageView)
        }

        fun setListener(listener: ImageCardClickListener) {
            mRootView.setOnLongClickListener(listener)
            mRootView.setOnClickListener(listener)
        }

        fun setTag(tag: IllustsBean) {
            mRootView.tag = tag
            mCollected = tag.isIs_bookmarked
        }

        fun setAuthorAndTitle(iconUri: GlideUrl?, name: String, title: String) {
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
    @Deprecated("changed")
    interface CollectionCallback {
        fun isCollected(uri: GlideUrl?): Boolean
        fun setCollected(uri: GlideUrl?, collected: Boolean)
    }

    @Deprecated("changed")
    class CollectionCallbackImpl :
        CollectionCallback {

        var collected = false

        override fun isCollected(uri: GlideUrl?): Boolean {
            Log.d(TAG, "isCollected uri:$uri")
            return collected
        }

        override fun setCollected(uri: GlideUrl?, collected: Boolean) {
            Log.d(TAG, "setCollected uri:$uri")
            this.collected = collected
        }
    }

    class CardStruct {
        var imgUri: GlideUrl? = null
        var imageCount: String = "NaN"
        var imageTitle: String = "Untitled"
        var authorName: String = "N/A"
        var authorIcon: GlideUrl? = null
        var imageBean: IllustsBean? = null
    }
}