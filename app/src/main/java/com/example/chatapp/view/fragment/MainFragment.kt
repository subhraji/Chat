package com.example.chatapp.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.adapter.ViewPagerAdapter
import com.example.chatapp.view.activity.LoginActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        tabLayout.addTab(tabLayout.newTab().setText("Chat"))
        tabLayout.addTab(tabLayout.newTab().setText("Group"))

        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = ViewPagerAdapter(requireActivity(), requireActivity().supportFragmentManager,
            tabLayout.tabCount)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        val sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)

        logoutBtn.setOnClickListener {
            val editor = sharedPreference.edit()
            editor.putString("accessToken","")
            editor.putString("refreshToken","")
            editor.putString("userId","")
            editor.putBoolean("loginStatus",false)
            editor.apply()

            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()

        }


        super.onViewCreated(view, savedInstanceState)
    }

}