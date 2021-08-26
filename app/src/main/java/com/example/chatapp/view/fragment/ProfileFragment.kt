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
import com.example.chatapp.R
import com.example.chatapp.helper.loadingDialog
import com.example.chatapp.model.repo.Outcome
import com.example.chatapp.viewmodel.GetProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private val getProfileViewModel: GetProfileViewModel by viewModel()
    lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = "JWT "+sharedPreference.getString("accessToken","name").toString()

        getProfile()
    }

    private fun getProfile(){
        val loader = requireActivity().loadingDialog()
        loader.show()
        getProfileViewModel.getProfile(accessToken).observe(viewLifecycleOwner, { outcome ->
            loader.dismiss()
            when(outcome){
                is Outcome.Success ->{
                    if (outcome.data.status == "success"){
                        val profileData = outcome.data.user
                        val name = profileData.username
                        val number = profileData.phoneno
                        val avatar = profileData.avatar




                        if (name != null){
                            profile_name_edit_txt.text = Editable.Factory.getInstance().newEditable(name.toString())
                        }else{
                            profile_name_edit_txt.text = Editable.Factory.getInstance().newEditable("update your name ")
                        }

                        mobile_edit_txt.text = Editable.Factory.getInstance().newEditable(number)
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