package com.example.chatapp.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chatapp.R
import com.example.chatapp.adapter.AddContactGroupAdapter
import com.example.chatapp.helper.gone
import com.example.chatapp.helper.visible
import com.example.chatapp.model.pojo.sync_contacts.User
import com.example.chatapp.model.repo.Outcome
import com.example.chatapp.viewmodel.AddGroupMembersViewModel
import com.example.chatapp.viewmodel.SyncContactsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_add_contact_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddContactListFragment : BottomSheetDialogFragment() {
    private val syncContactsViewModel: SyncContactsViewModel by viewModel()
    private val addGroupMembersViewModel: AddGroupMembersViewModel by viewModel()
    lateinit var accessToken: String
    lateinit var sharedPreference:SharedPreferences
    private lateinit var groupId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getString("groupId").toString()
        }
        Log.i("groupIdAg",groupId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_contact_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = "JWT "+sharedPreference.getString("accessToken","name").toString()
        getContacts("+917002838640")

        add_contacts_to_grp_btn.setOnClickListener {
            addGroupMember()
        }
    }

    private fun getContacts(phoneno: String){
        progressBar_ag.visible()
        syncContactsViewModel.syncContacts(phoneno.takeLast(10),accessToken).observe(viewLifecycleOwner, androidx.lifecycle.Observer { outcome->
            progressBar_ag.gone()
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data.status =="success"){
                        val user = outcome.data.user
                        val userList = listOf<User>(user)
                        add_contact_to_group_recycler.adapter = AddContactGroupAdapter(userList,requireActivity())
                        Log.i("clUser",user.toString())
                    }else{
                        Toast.makeText(activity,"error !!!", Toast.LENGTH_SHORT).show()
                    }
                }

                is Outcome.Failure<*> -> {
                    Toast.makeText(activity,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun addGroupMember(){
        progressBar_ag.visible()

        val set: Set<String> = sharedPreference.getStringSet("newCntList", null)!!
        Log.i("checkParams", set.toList().toString())

        addGroupMembersViewModel.addGroupMembers(groupId, set.toList(), accessToken).observe(viewLifecycleOwner, androidx.lifecycle.Observer { outcome->
            progressBar_ag.gone()
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data.status =="success"){
                        val user = outcome.data.group
                        Toast.makeText(activity,"Added successfully!!!", Toast.LENGTH_SHORT).show()
                        val editor = sharedPreference.edit()
                        val set: MutableSet<String> = HashSet()
                        set.addAll(emptyList())
                        editor.putStringSet("newCntList", set)
                        editor.apply()

                        dismiss()

                    }else{
                        Toast.makeText(activity,"error !!!", Toast.LENGTH_SHORT).show()
                    }
                }

                is Outcome.Failure<*> -> {
                    Toast.makeText(activity,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddContactListFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}