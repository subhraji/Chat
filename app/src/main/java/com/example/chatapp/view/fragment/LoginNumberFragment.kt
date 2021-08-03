package com.example.chatapp.view.fragment
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.chatapp.R
import com.example.chatapp.helper.loadingDialog
import com.example.chatapp.model.repo.Outcome
import com.example.chatapp.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login_number.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginNumberFragment : Fragment() {
    private lateinit var navController: NavController
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_number, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navController = Navigation.findNavController(requireActivity(), R.id.login_nav_host_fragment)

        getOtpBtn.setOnClickListener {
            val loader = requireActivity().loadingDialog()
            loader.show()
            loginViewModel.reqOtp(mobile_number_txt.text.toString()).observe(viewLifecycleOwner, androidx.lifecycle.Observer { outcome->
                loader.dismiss()
                when(outcome){
                    is Outcome.Success ->{
                        if(outcome.data.status =="success"){

                            val bundle = bundleOf("phoneno" to mobile_number_txt.text.toString())
                            navController.navigate(R.id.action_loginNumberFragment_to_loginOtpFragment, bundle)

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
