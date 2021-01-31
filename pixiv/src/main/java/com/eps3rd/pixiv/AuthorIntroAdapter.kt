package com.eps3rd.pixiv

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.eps3rd.app.R

class AuthorIntroAdapter : RecyclerView.Adapter<AuthorIntroAdapter.AuthorVH>() {


    companion object {
        const val TAG = "AuthorIntroAdapter"
    }

    private val mImageItems = ArrayList<AuthorStruct>()

    fun addItem(item: AuthorStruct){
        mImageItems.add(item)
        notifyItemInserted(mImageItems.size)
    }

    fun addItems(items: MutableList<AuthorStruct>){
        mImageItems.addAll(items)
        notifyItemRangeInserted(mImageItems.size,items.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorVH {
        return AuthorVH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_author_introduce, parent,false)
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
        private var mFollowButton: TextView
        private var mImageClickListener = ImageCardClickListener()


        val mStruct : AuthorStruct = AuthorStruct(ImageCardAdapter.ImageCardVH.NO_IMG_URI)

        init {
            mImages.add(rootView.findViewById<ImageView>(R.id.img_1).apply{
                this.setOnClickListener(mImageClickListener)
            })
            mImages.add(rootView.findViewById<ImageView>(R.id.img_2).apply{
                this.setOnClickListener(mImageClickListener)
            })
            mImages.add(rootView.findViewById<ImageView>(R.id.img_3).apply{
                this.setOnClickListener(mImageClickListener)
            })
            mAuthorIcon = rootView.findViewById(R.id.icon)
            mAuthorName = rootView.findViewById(R.id.author_name)
            mFollowButton = rootView.findViewById(R.id.follow_btn)

            mFollowButton.setOnClickListener {
                Log.d(TAG, "click mFollowButton")
            }
        }

        private fun loadImg(imgView: ImageView, uri: Uri) {
            val transform = if (imgView.id == R.id.icon){
                GlideCircleBorderTransform(
                    2,
                    imgView.context.resources.getColor(R.color.black))
            }else{
                CenterCrop()
            }
            Glide.with(imgView)
                .load(uri)
                .placeholder(R.drawable.ic_round_image_search_24)
                .error(R.drawable.ic_round_broken_image_24)
                .transform(transform)
                .into(imgView)
        }

        fun notifyDataChanged() {
            Log.d(TAG,"icon:${mStruct.icon}")
            loadImg(mAuthorIcon,mStruct.icon)
            mAuthorName.text = mStruct.authorName
            for ((i, uri) in mStruct.imgUris.withIndex()) {
                if (i > 2)
                    return
                Log.d(TAG,"img$i:${uri}")
                loadImg(mImages[i], uri)
            }
        }

    }


    data class AuthorStruct(
        var icon: Uri
    ) {
        var imgUris: MutableList<Uri> = ArrayList(3)
        var authorName: String = "N/A"
    }
}