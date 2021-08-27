package com.example.chatapp.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chatapp.R
import com.example.chatapp.model.repo.Outcome
import com.example.chatapp.viewmodel.GetProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.bumptech.glide.Glide

import com.bumptech.glide.request.RequestOptions
import com.example.chatapp.helper.*
import com.example.chatapp.viewmodel.UpdateProfileViewModel
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.update_number_layout.view.*


class ProfileFragment : Fragment() {
    private val getProfileViewModel: GetProfileViewModel by viewModel()
    private val updateProfileViewModel: UpdateProfileViewModel by viewModel()

    lateinit var accessToken: String
    private var userName: String? = null
    private var userNumber: String? = null
    private var userAvatar: String? = null


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

        profile_name_edit_txt.isEnabled = false
        mobile_edit_txt.isEnabled = false
        update_profile_btn.gone()

        val sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = "JWT "+sharedPreference.getString("accessToken","name").toString()

        getProfile()

        PushDownAnim.setPushDownAnimTo(profile_name_edit_btn).setOnClickListener {
            profile_name_edit_txt.isEnabled = true
            profile_name_edit_txt.showKeyboard()
            update_profile_btn.visible()
        }

        PushDownAnim.setPushDownAnimTo(profile_mobile_edit_btn).setOnClickListener {
            showDialog()
        }

        PushDownAnim.setPushDownAnimTo(profile_image_edit_btn).setOnClickListener {
            selectImage()
            update_profile_btn.visible()
        }

        update_profile_btn.setOnClickListener {
           updateProfile()
            requireActivity().hideSoftKeyboard()
        }

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


                        val options: RequestOptions = RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .error(R.drawable.ic_baseline_person_24)

                        if(avatar!=null){
                            Glide.with(this).load(avatar.toString()).apply(options)
                                .into(profile_image)
                            userAvatar = avatar.toString()
                        }else{
                            userAvatar = null
                        }

                        if (name != null){
                            profile_name_edit_txt.text = Editable.Factory.getInstance().newEditable(name.toString())
                            userName = name.toString()

                        }else{
                            profile_name_edit_txt.hint = "Update your name"
                            userName = null
                        }

                        mobile_edit_txt.text = Editable.Factory.getInstance().newEditable(number)
                        userNumber = number
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


    private fun updateProfile(){
        val loader = requireActivity().loadingDialog()
        loader.show()
        updateProfileViewModel.updateProfile(
            profile_name_edit_txt.text.toString(),
            userNumber.toString(),
            userAvatar.toString(),
            mobile_edit_txt.text.toString(),
            accessToken
        ).observe(viewLifecycleOwner,{outcome ->
            loader.dismiss()
            when(outcome){
                is Outcome.Success ->{
                    if (outcome.data.status == "success"){
                        val user = outcome.data.user
                        if (user.username!=null){
                            profile_name_edit_txt.text = Editable.Factory.getInstance().newEditable(user.username.toString())
                            profile_name_edit_txt.isEnabled = false
                            update_profile_btn.gone()
                        }

                        if (user.avatar!=null){
                            val options: RequestOptions = RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .error(R.drawable.ic_baseline_person_24)
                            Glide.with(this).load(user.avatar.toString()).apply(options)
                                .into(profile_image)
                        }
                    }
                }

                is Outcome.Failure ->{

                }
            }
        })
    }

    private fun selectImage() {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Add Photo!")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            when (options[item]) {
                "Take Photo" -> {
                    //dispatchCameraIntent()
                }
                "Choose from Gallery" -> {
                   // dispatchGalleryIntent()
                }
                "Cancel" -> {
                    dialog.dismiss()
                }
            }
        })
        builder.show()
    }

    private fun showDialog() {
        val dialogBox = LayoutInflater.from(activity).inflate(R.layout.update_number_layout, null)

        //AlertDialogBuilder
        val dialogBoxBuilder = AlertDialog.Builder(activity).setView(dialogBox)

        //setting text values
        dialogBox.update_num_edtxt_dialog.showKeyboard()


        dialogBox.get_otp_num_dialog_btn.setOnClickListener {

        }

        //show dialog
        val  dialogBoxInstance = dialogBoxBuilder.show()

        //set Listener
        dialogBox.setOnClickListener(){
            //close dialog
            dialogBoxInstance.dismiss()
        }
    }

}