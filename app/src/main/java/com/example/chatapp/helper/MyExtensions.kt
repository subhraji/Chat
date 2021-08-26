package com.example.chatapp.helper
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.text.Editable
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.chatapp.R
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.loadingdialog.view.*
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
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

fun ViewGroup.inflate(layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(this.context).inflate(layoutRes, this, attachToRoot)
}

fun Context.isTablet(): Boolean {
    return false
}

fun Activity.transparentStatusBar() {
    if (!isTablet()) {
        /*this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            window.statusBarColor = ContextCompat.getColor(this, R.color.purple_700)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        }
    }
}
fun Window.removeStatusBar() {

    if (this.context.isTablet()) {
        this.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

}

fun Activity.hideSoftKeyboard() {
    val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputMethodManager.isActive) {
        if (this.currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
    }
}


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun AlertDialog.invisibleBg() {
    this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}

fun Dialog.invisibleBg() {
    this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}

fun Activity.loadingDialog(cancelable: Boolean = false, lottieFile: Int? = null): AlertDialog {
    val nullParent: ViewGroup? = null
    val inflater = this.layoutInflater
    val alertLayout = inflater.inflate(R.layout.loadingdialog, nullParent)
    val loading = AlertDialog.Builder(this)
        .setView(alertLayout)
        .setCancelable(cancelable)
        .create()
    loading.invisibleBg()
    if (lottieFile != null) {
        alertLayout.loadingAnimation.setAnimation(lottieFile)
    }
    return loading
}

fun String.toMultipartFormString(): RequestBody {
    return this.toRequestBody(MultipartBody.FORM)
}

fun Context.createMultiPart(keyName: String, photoPath: File): MultipartBody.Part {
    //val file = File(photoPath)
    //Log.i("xxx ",file.toString())

    //val compressedImageFile = Compressor.compress(this, file)
    val requestFile = photoPath.asRequestBody("image/*".toMediaTypeOrNull())
    Log.i("xxx ",requestFile.toString())
    // MultipartBody.Part is used to send also the actual file name
    return MultipartBody.Part.createFormData(keyName, photoPath.name, requestFile)

}


fun ImageView.loadImg(path: Any, context: Context, placeholder: Drawable? = null) {
    Glide.with(context)
        .load(path)
        .placeholder(placeholder)
        .into(this)
}

fun Date.getTimeOnly(pattern: String = "hh:mm aa"): String {

    val sdf = SimpleDateFormat(pattern, Locale.US)
    return sdf.format(this)
}
