package com.example.chatapp.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.adapter.ViewPagerAdapter
import com.example.chatapp.helper.NewGroupListener
import com.example.chatapp.view.activity.LoginActivity
import com.google.android.material.tabs.TabLayout
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment() : Fragment() {

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
        tabLayout.addTab(tabLayout.newTab().setText("Status"))


        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = ViewPagerAdapter(requireActivity(), childFragmentManager,
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


        PushDownAnim.setPushDownAnimTo(main_options_btn).setOnClickListener {

            val popupMenu:PopupMenu = PopupMenu(requireContext(),main_options_btn)
            popupMenu.menuInflater.inflate(R.menu.main_fragment_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {

                when(it.itemId){
                    R.id.logout_menu -> {
                        logout()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.settings_menu -> {
                        Toast.makeText(requireActivity(),"settings is coming soon",Toast.LENGTH_SHORT).show()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.profile_menu -> {
                        findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
                        return@setOnMenuItemClickListener true
                    }
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun logout(){
        val sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)

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

}