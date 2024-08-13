package com.sendbird.uikit.compose.sample

import android.app.Application
import com.sendbird.android.push.SendbirdPushHelper
import com.sendbird.uikit.compose.SendbirdUikitCompose
import com.sendbird.uikit.compose.sample.fcm.MyFirebaseMessagingService
import com.sendbird.uikit.compose.sample.pref.SendbirdUikitPref
import com.sendbird.uikit.core.data.model.UikitInitParams
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val pref = SendbirdUikitPref(this)
        // Initialize Sendbird UIKit
        SendbirdUikitCompose.init((UikitInitParams(
            appId = pref.appId,
            context = this,
            isForeground = true
        ))).launchIn(MainScope())

        // Register push notification handler
        SendbirdPushHelper.registerHandler(MyFirebaseMessagingService())
    }
}
