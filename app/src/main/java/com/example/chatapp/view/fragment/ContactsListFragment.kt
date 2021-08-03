package com.example.chatapp.view.fragment

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.example.chatapp.R
import com.example.chatapp.adapter.ContactListAdapter
import com.example.chatapp.helper.loadingDialog
import com.example.chatapp.model.pojo.Contacts
import com.example.chatapp.model.pojo.sync_contacts.User
import com.example.chatapp.model.repo.Outcome
import com.example.chatapp.viewmodel.SyncContactsViewModel
import kotlinx.android.synthetic.main.fragment_contacts_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ContactsListFragment : Fragment() {
    private val syncContactsViewModel: SyncContactsViewModel by viewModel()
    lateinit var accessToken: String
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100

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

        loadContacts()
        syncContacts("+917002838640")
    }


    private fun loadContacts() {
        var builder = StringBuilder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(requireActivity(),
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS)
            //callback onRequestPermissionsResult
        } else {
           getContactList()
            //listContacts.text = builder.toString()
        }
    }

    var contactList: ArrayList<Contacts> = ArrayList()

    private val PROJECTION = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )
    private fun getContactList() {
        val cr: ContentResolver = requireActivity().contentResolver
        val cursor: Cursor? = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PROJECTION,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor != null) {
            val mobileNoSet = HashSet<String>()
            try {
                val nameIndex: Int = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val numberIndex: Int =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                var name: String
                var number: String
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex)
                    number = cursor.getString(numberIndex)
                    number = number.replace(" ", "")
                    if (!mobileNoSet.contains(number)) {
                        contactList.add(Contacts(name, number))
                        mobileNoSet.add(number)
                        Log.d(
                            "hvy", "onCreaterrView  Phone Number: name = " + name
                                    + " No = " + number
                        )
                        /*contactList.forEach {
                            syncContacts(number)
                        }*/
                    }
                }
            } finally {
                cursor.close()
            }
        }
    }

    private fun syncContacts(phoneno: String){
        val loader = requireActivity().loadingDialog()
        loader.show()
        syncContactsViewModel.syncContacts(phoneno.takeLast(10),accessToken).observe(viewLifecycleOwner, androidx.lifecycle.Observer { outcome->
            loader.dismiss()
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data.status =="success"){
                        val user = outcome.data.user
                        val userList = listOf<User>(user)
                        contactListRecycler.adapter = ContactListAdapter(userList,requireActivity())
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                Toast.makeText(activity,"Permission must be granted in order to display contacts information",Toast.LENGTH_SHORT).show()
            }
        }
    }
}