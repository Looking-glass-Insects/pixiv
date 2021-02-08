package com.eps3rd.pixiv

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.eps3rd.app.R
import com.eps3rd.pixiv.transform.GlideCircleBorderTransform
import com.google.android.material.chip.Chip

class AuthorIntroAdapter : RecyclerView.Adapter<AuthorIntroAdapter.AuthorVH>() {


    companion object {
        const val TAG = "AuthorIntroAdapter"
    }

    private val mImageItems = ArrayList<AuthorStruct>()

    fun addItem(item: AuthorStruct) {
        mImageItems.add(item)
        notifyItemInserted(mImageItems.size)
    }

    fun addItems(items: MutableList<AuthorStruct>) {
        mImageItems.addAll(items)
        notifyItemRangeInserted(mImageItems.size, items.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorVH {
        return AuthorVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_author_introduce, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mImageItems.size
    }

    override fun onBindViewHolder(holder: AuthorVH, position: Int) {
        val item = mImageItems[position]
        holder.mStruct.icon = item.icon
        holder.mStruct.imgUris.clear()
        holder.mStruct.imgUris.addAll(item.imgUris)
        holder.mStruct.authorName = item.authorName
        holder.notifyDataChanged()
    }


    class AuthorVH(rootView: View) : RecyclerView.ViewHolder(rootView) {
        private var mImages: MutableList<ImageView> = ArrayList(3)
        private var mAuthorIcon: ImageView
        private var mAuthorName: TextView
        private var mAuthorBgImg: ImageView
        private var mFollowButton: Chip
        private var mImageClickListener = ImageCardClickListener()
        private var mAuthorIconClickListener = AuthorIconClickListener()
        private var mImgGroup: Group


        var mStruct: AuthorStruct = AuthorStruct(ImageCardAdapter.ImageCardVH.NO_IMG_URI)

        init {
            mImages.add(rootView.findViewById<ImageView>(R.id.img_1).apply {
                this.setOnClickListener(mImageClickListener)
                this.setOnLongClickListener(mImageClickListener)
            })
            mImages.add(rootView.findViewById<ImageView>(R.id.img_2).apply {
                this.setOnClickListener(mImageClickListener)
                this.setOnLongClickListener(mImageClickListener)
            })
            mImages.add(rootView.findViewById<ImageView>(R.id.img_3).apply {
                this.setOnClickListener(mImageClickListener)
                this.setOnLongClickListener(mImageClickListener)
            })
            mAuthorIcon = rootView.findViewById<ImageView>(R.id.icon).apply {
                this.setOnClickListener(mAuthorIconClickListener)
                this.setOnLongClickListener(mImageClickListener)
            }
            mAuthorName = rootView.findViewById(R.id.author_name)
            mFollowButton = rootView.findViewById(R.id.follow_btn)

            mAuthorBgImg = rootView.findViewById(R.id.author_bg_img)
            mImgGroup = rootView.findViewById(R.id.author_imgs)

            mFollowButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    mFollowButton.setText(R.string.follow_btn_following)
                } else {
                    mFollowButton.setText(R.string.follow_btn_follow)
                }
            }
        }

        private fun loadImg(imgView: ImageView, uri: Uri) {
            val transform = if (imgView.id == R.id.icon) {
                GlideCircleBorderTransform(
                    2,
                    imgView.context.resources.getColor(R.color.black)
                )
            } else {
                CenterCrop()
            }
            imgView.tag = uri
            GlideApp.with(imgView)
                .load(uri)
                .placeholder(R.drawable.ic_round_image_search_24)
                .error(R.drawable.ic_round_broken_image_24)
                .transform(transform)
                .into(imgView)
        }

        fun notifyDataChanged() {
            Log.d(TAG, "icon:${mStruct.icon}")
            loadImg(mAuthorIcon, mStruct.icon)
            mAuthorName.text = mStruct.authorName
            for ((i, uri) in mStruct.imgUris.withIndex()) {
                if (i > 2)
                    return
                Log.d(TAG, "img$i:${uri}")
                loadImg(mImages[i], uri)
            }
            mStruct.bgImgUri?.also {
                loadImg(mAuthorBgImg, mStruct.bgImgUri!!)
            }
        }

        fun hideImages() {
            mImgGroup.visibility = View.GONE
        }
    }


    data class AuthorStruct(
        var icon: Uri
    ) {
        var imgUris: MutableList<Uri> = ArrayList(5)
        var authorName: String = "N/A"
        var bgImgUri: Uri? = null
    }
}