package com.example.chatapp.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.chatapp.R
import com.example.chatapp.model.repo.Outcome
import com.example.chatapp.viewmodel.SyncContactsViewModel
import com.example.chatapp.viewmodel.VerifyOtpViewModel
import kotlinx.android.synthetic.main.fragment_login_number.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactsListFragment : Fragment() {
    private val syncContactsViewModel: SyncContactsViewModel by viewModel()
    lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = "JWT "+sharedPreference.getString("accessToken","name").toString()

        syncContacts()
    }

    private fun syncContacts(){
        syncContactsViewModel.syncContacts("8011299668",accessToken).observe(viewLifecycleOwner, androidx.lifecycle.Observer { outcome->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data.status =="success"){
                        Toast.makeText(activity,outcome.data.user.phoneno, Toast.LENGTH_SHORT).show()
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
}