package com.example.chatapp.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.chatapp.view.fragment.ChatListFragment
import com.example.chatapp.view.fragment.GroupListFragment
import java.util.*

class ViewPagerAdapter(var context: Context,
                       fm: FragmentManager,
                       var totalTabs: Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ChatListFragment()
            }
            1 -> {
                GroupListFragment()
            }

            else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }

}