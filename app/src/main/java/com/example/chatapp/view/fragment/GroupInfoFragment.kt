package com.example.chatapp.view.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatapp.R
import com.example.chatapp.model.repo.Outcome
import com.example.chatapp.viewmodel.GetGroupMembersViewModel
import com.example.chatapp.viewmodel.GetProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupInfoFragment : Fragment() {

    lateinit var accessToken: String
    lateinit var groupId: String

    private val getGroupMembersViewModel: GetGroupMembersViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getString("groupId").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = "JWT "+sharedPreference.getString("accessToken","name").toString()

        getGroupMembers()
    }

    private fun getGroupMembers(){
        getGroupMembersViewModel.getGroupMembers(accessToken,groupId).observe(viewLifecycleOwner, { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if (outcome.data.status == "success"){
                        Toast.makeText(activity,"Success !!!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(activity,"error !!!", Toast.LENGTH_SHORT).show()
                    }
                }

                is Outcome.Failure<*> ->{
                    Toast.makeText(activity,outcome.e.message, Toast.LENGTH_SHORT).show()
                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })

    }

}