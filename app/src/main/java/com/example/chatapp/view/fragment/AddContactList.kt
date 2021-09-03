package com.example.chatapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatapp.R
import com.example.chatapp.adapter.AddContactGroupAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_add_contact_list.*

data class ContactList(
    val id: Int,
    val name: String,
)
class AddContactList : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
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
        fillRecyclerView(dummylist)
    }

    private fun fillRecyclerView(contactList: List<ContactList>) {
        add_contact_to_group_recycler.adapter = AddContactGroupAdapter(dummylist)
    }
    private val dummylist = listOf<ContactList>(
        ContactList(1,"600997"),
        ContactList(2,"54657678"),
        ContactList(3,"412269968"),
    )




    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddContactList().apply {
                arguments = Bundle().apply {}
            }
    }
}