package com.eps3rd.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainActivityPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val mTabFragmentList: MutableList<Fragment> = ArrayList()
    private val  mPageIds : MutableList<Long> = ArrayList()


    fun addItem(fragment: Fragment){
        mTabFragmentList.add(0,fragment)
        mPageIds.add(0,fragment.hashCode().toLong())
        notifyItemInserted(0)
    }

    fun removeItem(position: Int){
        mTabFragmentList.removeAt(position)
        mPageIds.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItem(position: Int): Fragment{
        return mTabFragmentList[position]
    }

    override fun getItemCount(): Int {
        return mTabFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mTabFragmentList[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return mPageIds.contains(itemId)
    }

    override fun getItemId(position: Int): Long {
        return mTabFragmentList[position].hashCode().toLong()
    }

}