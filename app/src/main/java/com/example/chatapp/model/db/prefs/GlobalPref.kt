 package com.example.chatapp.model.db.prefs

import android.content.Context
import android.content.SharedPreferences


 class GlobalPref(context: Context) {

    // Shared Preferences
    private var pref: SharedPreferences

    // Editor for Shared preferences
    private var editor: SharedPreferences.Editor

    // Shared pref mode
    private var PRIVATE_MODE = 0

    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    /**
     * Get stored session data
     */

    fun getEditor(): SharedPreferences.Editor{
        return editor
    }

    fun saveData(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBooleanData(key: String): Boolean {
        return pref.getBoolean(key, false)
    }

    fun getData(key: String): String? {
        return pref.getString(key, null)
    }

    fun getIntData(key: String): Int {
        return pref.getInt(key, 1)
    }

    // Get Login State
    val isTokenSaved: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)

    // Save Token
    fun saveData(key: String, value: String) {

        editor.putBoolean(IS_LOGIN, true)

        editor.putString(key, value)
        editor.apply()
    }

    fun saveData(key: String, value: Int) {

        editor.putBoolean(IS_LOGIN, true)

        editor.putInt(key, value)
        editor.apply()
    }



    // clear token
    fun clear() {

        // Clearing all data from Shared Preferences
        editor.clear()
        editor.commit()
    }

    companion object {

        // Sharedpref file name
        private const val PREF_NAME = "global_pref"

        // All Shared Preferences Keys
        private const val IS_LOGIN = "IsLoggedIn"
        const val HAS_LOCKED_DATA = "has_device_lock_data"
        const val STUDENT_NAME = "student_name"
        const val KEY_ORDER_ID = "order_id"
        const val KEY_REF_ID = "ref_id"
        const val KEY_VIDEO_URL = "video_url"
        const val KEY_REFER_CODE = "refer_code"

        const val DEVICE_LOCK_DOWN = "device_lock_down"
        const val STATUS_AVAILABLE="status_available"
        const val IS_WORK_MANAGER_STARTED="is_work_manager_started"

        const val PRIVACY_ALLOW_FRND_REQUEST = "allow_friend_request"
        const val PRIVACY_SHARE_STREAM = "share_stream"

        const val IMEI = "imei"
        const val EMAIL = "email"
        const val EMAIL_VERIFIED = "email_verified"
        const val MOBILE_NUMBER = "mobile_no"


        const val JSON_FILE_CONTENT_PLAN = "json_content"
        const val JSON_FILE_CONTENT_DEVICE = "json_device"

        const val VIDEO_PROGRESS = "video_progress"
        const val NOTIFICATION_PREF = "notification_pref"
        const val NOTIFICATION_SEEN = "status"

        const val MAJOR_UPDATE_AVAILABLE = "major_update_available"
        const val MAJOR_UPDATE_DATA = "major_update_data"

        const val PREVIOUS_ACTIVITY = "previous_activity"
        const val MAIN_ACTIVITY = "main_activity"

        //Todo delete this below
        const val ANALYTICS = "quiz_analytics"

        const val HALF_SYNCED = "half_synced"

        const val IS_TRIAL = "is_trail"

        const val FRIEND_CHAT_ALLOWED = "friend_chat_allowed"

        const val TRAIL_EXPIRES_ON = "trail_expires_on"

        const val IS_SYNCING = "is_syncing"

        const val PLAY_REFERRER_DONE = "play_referrer_done"

    }
}