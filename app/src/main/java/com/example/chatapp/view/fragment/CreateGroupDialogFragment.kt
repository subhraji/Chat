package com.example.chatapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.chatapp.R
import com.example.chatapp.helper.NewGroupListener
import kotlinx.android.synthetic.main.fragment_creaye_grop_dialog.*

class CreateGroupDialogFragment(newGroupListener: NewGroupListener) : DialogFragment() {

    private val groupListener = newGroupListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_creaye_grop_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        create_group_btn.setOnClickListener {
            groupListener.createNewGroup()
        }

    }

}