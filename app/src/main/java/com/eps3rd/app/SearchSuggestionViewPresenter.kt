package com.eps3rd.app

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class SearchSuggestionViewPresenter(rootView: ViewGroup) {

    companion object{
        const val TAG = "SearchSuggestionViewPresenter"
        const val KEY_SUGGESTIONS = "KEY_SUGGESTIONS"
    }

    private var mContext: Context
    private var mSuggestionContainer:ViewGroup
    private var mClearSuggestionView: View
    private var mRvSuggestion: RecyclerView
    private val mAdapter = SuggestionItemAdapter()
    var mClickListener: OnClickListener? = null

    init {
        mContext = rootView.context
        mSuggestionContainer = rootView
        mClearSuggestionView = rootView.findViewById(R.id.tv_clear_suggestion)
        mRvSuggestion = rootView.findViewById(R.id.rv_search_suggestion)
        mRvSuggestion.layoutManager = LinearLayoutManager(mContext)
        mRvSuggestion.adapter = mAdapter
        mAdapter.presenter = this

        mClearSuggestionView.setOnClickListener {
            clearHistory()
        }
    }

    fun addFilterString(query: String) {
        Log.d(TAG,"addFilterString:$query")
    }

    private fun removeItem(position: Int){
        Log.d(TAG,"removeItem:$position")
        mAdapter.mItems.removeAt(position)
        mAdapter.notifyItemRemoved(position)
    }

    fun showSuggestion() {
        Log.d(TAG,"showSuggestion")
        GlobalScope.launch(Dispatchers.IO){
            val kv: MMKV = MMKV.defaultMMKV()!!
            val set = kv.decodeStringSet(KEY_SUGGESTIONS)
            Log.d(TAG,"showSuggestion$set")
            if (set == null || set.isEmpty())
                return@launch
            for (s in set){
                mAdapter.mItems.add(s)
            }
            withContext(Dispatchers.Main){
                mAdapter.notifyDataSetChanged()
                mSuggestionContainer.visibility = View.VISIBLE
            }
        }
    }

    fun addItem(query: String){
        Log.d(TAG,"addItem:$query")
        mAdapter.mItems.add(query)
        mAdapter.notifyItemInserted(mAdapter.mItems.size)
    }

    fun hideAndSaveSuggestion() {
        Log.d(TAG,"hideAndSaveSuggestion")
        GlobalScope.launch(Dispatchers.IO){
            val kv: MMKV = MMKV.defaultMMKV()!!
            Log.d(TAG,"hide:${mAdapter.mItems.toSet()}")
            kv.encode(KEY_SUGGESTIONS, mAdapter.mItems.toSet())
            withContext(Dispatchers.Main){
                mSuggestionContainer.visibility = View.GONE
                mAdapter.mItems.clear()
            }
        }
    }

    private fun clearHistory() {
        Log.d(TAG,"clearHistory")
        mAdapter.mItems.clear()
        mAdapter.notifyDataSetChanged()
    }


    class SuggestionItemAdapter : RecyclerView.Adapter<SuggestionItemVH>() {

        var mItems = ArrayList<String>()
        var presenter: SearchSuggestionViewPresenter? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionItemVH {
            return SuggestionItemVH(
                LayoutInflater.from(parent.context).inflate(R.layout.item_suggestion, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return mItems.size
        }

        override fun onBindViewHolder(holder: SuggestionItemVH, position: Int) {
            holder.mTv.text = mItems[position]
            holder.mContainer.setOnClickListener {
                presenter?.mClickListener?.onClick(mItems[position])
            }
            holder.mClearBtn.setOnClickListener {
                presenter?.removeItem(position)
            }
        }
    }

    class SuggestionItemVH(rootView: View) : RecyclerView.ViewHolder(rootView) {
        var mTv: TextView
        var mClearBtn: ImageView
        var mContainer: ViewGroup

        init {
            mContainer = rootView.findViewById(R.id.container)
            mTv = rootView.findViewById(R.id.tv_suggestion)
            mClearBtn = rootView.findViewById(R.id.delete)
        }
    }

    interface OnClickListener {
        fun onClick(query: String)
    }

}