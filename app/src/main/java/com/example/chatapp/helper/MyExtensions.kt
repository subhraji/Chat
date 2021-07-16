package com.example.chatapp.helper
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.chatapp.R
import java.util.*

fun Context.showErrorMsg(): String {
    return if (isConnectedToInternet()) {
        getString(R.string.error_loading)
    } else {
        getString(R.string.not_connected_to_internet)
    }
}

fun Context.isConnectedToInternet(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.isConnected ?: false
}
