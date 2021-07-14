package com.cangjiedata.baselibrary.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

class ViewPagerAdapter(fm: FragmentManager?, fragments: List<Fragment>?) :
    FragmentStatePagerAdapter(
        fm!!
    ) {
    var fragments: List<Fragment>? = ArrayList()
    override fun getItem(position: Int): Fragment {
        return fragments!![position]
    }

    override fun getCount(): Int {
        return if (fragments == null) 0 else fragments!!.size
    }

    init {
        this.fragments = fragments
    }
}