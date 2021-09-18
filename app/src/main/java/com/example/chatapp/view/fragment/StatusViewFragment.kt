package com.example.chatapp.view.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.chatapp.R
import com.example.chatapp.helper.gone
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_status_view.*
import kotlinx.android.synthetic.main.status_list_item.view.*
import android.os.CountDownTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class StatusViewFragment : Fragment() {
    private var imageUrl: String? = null
    private var userName: String? = null

    var mCountDownTimer: CountDownTimer? = null
    var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString("imageUrl").toString()
            userName = it.getString("userName").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        status_view_progress_bar.setProgress(i)
        mCountDownTimer = object : CountDownTimer(5000, 50) {
            override fun onTick(millisUntilFinished: Long) {
                Log.v("Log_tag", "Tick of Progress ${i as Int * 100 / (5000 / 50)}")
                i++
                status_view_progress_bar.setProgress(i as Int * 100 / (5000 / 50))
            }

            override fun onFinish() {
                //Do what you want
                i++
                //status_view_progress_bar.setProgress(100)
                findNavController().navigateUp()
            }
        }
        (mCountDownTimer as CountDownTimer).start()

        Glide.with(requireActivity())
            .load(imageUrl)
            .centerCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    status_image_progress.gone()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    status_image_progress.gone()
                    return false
                }
            })
            .into(status_img_view)

        status_view_username_txt.text = userName

        PushDownAnim.setPushDownAnimTo(status_view_back_btn).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}