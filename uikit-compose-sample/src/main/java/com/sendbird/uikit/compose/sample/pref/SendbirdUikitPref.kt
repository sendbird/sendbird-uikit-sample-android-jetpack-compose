package com.sendbird.uikit.compose.sample.pref

import android.content.Context
import android.content.SharedPreferences

private const val PREFERENCE_KEY_APP_ID = "PREFERENCE_KEY_APP_ID"
private const val PREFERENCE_KEY_USER_ID = "PREFERENCE_KEY_USER_ID"

/**
 * A class that manages the preferences of the Sendbird UIKit.
 */
class SendbirdUikitPref(context: Context, fileName: String = "sendbird-uikit-compose-sample") {
    var appId: String
        get() = getString(PREFERENCE_KEY_APP_ID) ?: "FEA2129A-EA73-4EB9-9E0B-EC738E7EB768"
        set(value) = putString(PREFERENCE_KEY_APP_ID, value)

    var userId: String
        get() = getString(PREFERENCE_KEY_USER_ID) ?: ""
        set(value) = putString(PREFERENCE_KEY_USER_ID, value)

    private fun getString(key: String, defaultValue: String? = null): String? =
        pref.getString(key, defaultValue) ?: defaultValue

    private fun putString(key: String, value: String) = pref.edit().putString(key, value).apply()

    private val pref: SharedPreferences by lazy {
        context.getSharedPreferences(
            fileName,
            Context.MODE_PRIVATE
        )
    }
}
