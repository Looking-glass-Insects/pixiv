package com.eps3rd.pixiv.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.eps3rd.pixiv.Constants
import com.eps3rd.app.R
import com.eps3rd.pixiv.IFragment
import com.eps3rd.pixiv.ImageCardAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.lang.IllegalStateException


@Route(path = Constants.FRAGMENT_PATH_COLLECTION)
class CollectionFragment : Fragment(), IFragment {

    companion object {
        const val TAG = "CollectionFragment"
        const val TYPE = "TYPE"
        const val TYPE_COLLECTION = "TYPE_COLLECTION"
        const val TYPE_AUTHOR_WORKS = "TYPE_AUTHOR_WORKS"

        object sOnCheckedListener : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                Log.d(TAG,"Tag Check:$isChecked,${buttonView?.tag}")
            }
        }


        fun buildTag(layoutInflater: LayoutInflater, text: String): Chip{
            val tag = layoutInflater.inflate(R.layout.collection_header_tag,null,false) as Chip
            tag.setText(text)
            tag.tag = text
            tag.setOnCheckedChangeListener(sOnCheckedListener)
            return tag
        }

    }


    private lateinit var mCollectionRV: RecyclerView
    private lateinit var mHeader: ViewGroup
    private val mCollectionAdapter = ImageCardAdapter()
    private var mTypeString: String? = null


    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        arguments?.let {
            mTypeString = it.getString(TYPE)
            Log.d(TAG, "type:$mTypeString")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection, container, false)
        mCollectionRV = view.findViewById(R.id.rv_collection)
        mHeader = view.findViewById(R.id.collection_header)

        mCollectionRV.layoutManager = GridLayoutManager(context, 2)
        mCollectionAdapter.mShowAuthor = false
        mCollectionRV.adapter = mCollectionAdapter

        when (mTypeString) {
            TYPE_COLLECTION -> {
                val viewGroup  = inflater.inflate(R.layout.collection_header_tags, null, false)
                val tags = viewGroup.requireViewById<ChipGroup>(R.id.tags)
                tags.addView(buildTag(inflater,"test1"))
                tags.addView(buildTag(inflater,"test2"))
                mHeader.addView(viewGroup)
            }
            TYPE_AUTHOR_WORKS -> {

            }
            else -> {
                throw IllegalStateException("collection fragment no type")
            }
        }


        // DEBUG CODE
        for (i in 0 until 5) {
            mCollectionAdapter.addItem(ImageCardAdapter.CardStruct(Uri.parse("android.resource://com.eps3rd.pixiv/" + R.mipmap.test)))
        }
        return view
    }

    override fun getFragmentTitle(): Int {
       return when(mTypeString){
           TYPE_COLLECTION -> R.string.fragment_collection
           TYPE_AUTHOR_WORKS -> R.string.fragment_collection_author
           else -> -1
       }
    }

}