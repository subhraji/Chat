package com.example.chatapp.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.helper.loadingDialog
import com.example.chatapp.model.repo.Outcome
import com.example.chatapp.viewmodel.VerifyOtpViewModel
import kotlinx.android.synthetic.main.fragment_login_number.*
import kotlinx.android.synthetic.main.fragment_login_otp.*
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val PHONE_NO = "phoneno"

class LoginOtpFragment : Fragment() {
    private var phoneno: String? = null
    private val verifyOtpViewModel: VerifyOtpViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            phoneno = it.getString(PHONE_NO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_otp, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        verify_otp_btn.setOnClickListener {
            val loader = requireActivity().loadingDialog()
            loader.show()
            verifyOtpViewModel.verifyOtp(phoneno.toString(), otp_txt.text.toString()).observe(viewLifecycleOwner, androidx.lifecycle.Observer { outcome->
                loader.dismiss()
                when(outcome){
                    is Outcome.Success ->{
                        if(outcome.data.status =="success"){

                            val accessToken = outcome.data.accessToken
                            val refreshToken = outcome.data.refreshToken
                            val userId = outcome.data.userId

                            val sharedPreference =  requireActivity().getSharedPreferences("TOKEN_PREF",
                                Context.MODE_PRIVATE)
                            val editor = sharedPreference.edit()
                            editor.putString("accessToken",accessToken)
                            editor.putString("refreshToken",refreshToken)
                            editor.putString("userId",userId)
                            editor.putBoolean("loginStatus",true)
                            editor.apply()

                            val intent = Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()

                        }else{
                            Toast.makeText(activity,"error !!!",Toast.LENGTH_SHORT).show()
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
}