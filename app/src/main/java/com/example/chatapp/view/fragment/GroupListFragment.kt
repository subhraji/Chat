package com.example.chatapp.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.adapter.GroupListAdapter
import com.example.chatapp.helper.SocketHelper
import com.example.chatapp.helper.gone
import com.example.chatapp.helper.loadingDialog
import com.example.chatapp.helper.visible
import com.example.chatapp.model.pojo.chat_user.ChatUser
import com.example.chatapp.model.pojo.create_group.Group
import com.example.chatapp.model.pojo.group_chat.GroupMessage
import com.example.chatapp.model.repo.Outcome
import com.example.chatapp.viewmodel.CreateGroupViewModel
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_group_list.*
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupListFragment() : Fragment(), GroupListAdapter.GroupItemClickClickLister {
    private var mSocket: Socket? = null
    lateinit var accessToken: String
    private val createGroupViewModel: CreateGroupViewModel by viewModel()
    private lateinit var mGroupListAdapter: GroupListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        create_group_card.gone()


        val sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = "JWT "+sharedPreference.getString("accessToken","name").toString()


        mGroupListAdapter = GroupListAdapter(mutableListOf(),requireActivity(),this@GroupListFragment)
        group_list_recycler.apply {
            adapter = mGroupListAdapter
        }

        initSocket()

        PushDownAnim.setPushDownAnimTo(add_group_btn).setOnClickListener {
            create_group_card.visible()
        }

        PushDownAnim.setPushDownAnimTo(create_group_btn).setOnClickListener {
            create_group_card.gone()
            createGroup(group_name_txt.text.toString(), "")
        }

        getGroups()

    }

    private fun initSocket() {
        while (mSocket == null) {
            mSocket = SocketHelper.loginSocket(requireContext())
        }
        //listeners
        mSocket?.let { socket ->
            socket.on("added group", addGroupListener)
        }
        mSocket?.connect()
        Log.d("Socket_connected_gr", "Socket_connected => ${mSocket?.connected()}")
    }


    private val addGroupListener = Emitter.Listener {
        requireActivity().runOnUiThread {
            val data = it[0] as JSONObject
            Log.d("group_data", "group_data1 => $data")
            try {
                val messageData = data.getJSONObject("data")
                val message = Gson().fromJson(messageData.toString(), Group::class.java)

                if(message!=null){
                    Log.d("group_data", "group_data_msg => $messageData")
                    val group = Group(
                        message.createdBy,
                        message.groupImage,
                        message.groupName,
                        message.id,
                        message.userGroup
                    )
                    saveGroup(group)
                    mGroupListAdapter.addGroup(group)


                }

            } catch (e: JSONException) {
                Log.d("Socket_connected_gr","group exception -> ${e.message}")
                e.printStackTrace()
            }
        }
    }


    private fun createGroup(group_name: String, group_image: String){
        val loader = requireActivity().loadingDialog()
        loader.show()
        createGroupViewModel.createGroup(group_name, group_image, accessToken).observe(requireActivity(), { outcome ->
            loader.dismiss()
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data.status =="success"){
                        val groupData = outcome.data.group
                        Toast.makeText(activity,"success !!!", Toast.LENGTH_SHORT).show()
                        val group = Group(
                            groupData.createdBy,
                            groupData.groupImage,
                            groupData.groupName,
                            groupData.id,
                            groupData.userGroup
                        )
                        saveGroup(group)
                        mGroupListAdapter.addGroup(group)
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

    private fun saveGroup(group: Group) {
        createGroupViewModel.saveGroup(group)
    }

    private fun getGroups(){
        createGroupViewModel.getGroups().observe(requireActivity(), { groups ->
            Log.d("userSize","user list size = ${groups.size}")
            if (!groups.isNullOrEmpty()) {
                Log.d("userSize","user list size too = ${groups.size}")

                //group_list_recycler.adapter = GroupListAdapter(groups.toMutableList(),requireActivity())
                mGroupListAdapter.addAllGroups(groups)

            } else {
                Log.d("userSize","user list size => ${groups.size}")
            }
        })
    }


    override fun onItemClicked(view: View, position: Int) {
        val groups = view.tag as Group
        val bundle = bundleOf("groupName" to groups.groupName, "groupId" to groups.id)
        findNavController().navigate(R.id.groupChatFragment, bundle)
    }
}