package com.sendbird.uikit.compose.sample

import android.content.Context
import android.content.Intent
import android.os.Process
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sendbird.android.SendbirdChat
import com.sendbird.android.ktx.extension.awaitUpdateCurrentUserInfo
import com.sendbird.android.ktx.extension.push.awaitUnregisterHandler
import com.sendbird.android.params.UserUpdateParams
import com.sendbird.android.push.SendbirdPushHelper
import com.sendbird.uikit.compose.SendbirdUikitCompose
import com.sendbird.uikit.compose.component.CircularProgressIndicator
import com.sendbird.uikit.compose.component.SendbirdSnackbar
import com.sendbird.uikit.compose.component.showError
import com.sendbird.uikit.compose.navigation.SendbirdNavigation
import com.sendbird.uikit.compose.navigation.navigateToChannel
import com.sendbird.uikit.compose.navigation.sendbirdGroupChannelNavGraph
import com.sendbird.uikit.compose.sample.channels.TabbedChannelsScreen
import com.sendbird.uikit.compose.sample.fcm.MyFirebaseMessagingService
import com.sendbird.uikit.compose.sample.login.LoginRoute
import com.sendbird.uikit.compose.sample.login.LoginScreen
import com.sendbird.uikit.compose.sample.pref.SendbirdUikitPref
import com.sendbird.uikit.core.data.model.UikitCurrentUserInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SendbirdUikitApp(
    intent: Intent? = null
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val appState = remember { SendbirdUikitSampleAppState(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val viewScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(appState.isLoggedIn) {
        if (appState.isLoggedIn) {
            SendbirdUikitCompose.prepare(
                UikitCurrentUserInfo(
                    userId = appState.pref.userId,
                )
            )
            SendbirdUikitCompose.connect()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                SendbirdSnackbar(data = it)
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = if (appState.isLoggedIn) SendbirdNavigation.GroupChannel.route else LoginRoute,
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            },
            modifier = Modifier.padding(it)
        ) {
            composable(route = LoginRoute) {
                LoginScreen(
                    defaultAppId = appState.pref.appId,
                    onSaveAppId = {
                        viewScope.launch {
                            appState.saveAppId(it)
                        }
                    },
                    onLogin = { userId, nickname ->
                        viewScope.launch {
                            isLoading = true
                            runCatching {
                                appState.login(userId, nickname)
                            }.onSuccess {
                                isLoading = false
                                navController.navigate(SendbirdNavigation.GroupChannel.route)
                            }.onFailure {
                                isLoading = false
                                snackbarHostState.showError("Failed to login")
                            }
                        }
                    }
                )
            }

            sendbirdGroupChannelNavGraph(
                navController = navController,
                channelsScreen = {
                    TabbedChannelsScreen(
                        navController = navController,
                        onLogoutClick = {
                            viewScope.launch {
                                runCatching {
                                    appState.logout()
                                }.onSuccess {
                                    navController.navigate(LoginRoute)
                                }.onFailure {
                                    snackbarHostState.showError("Failed to logout")
                                }
                            }
                        }
                    )
                }
            )
        }

        if (isLoading) {
            Dialog(
                onDismissRequest = { isLoading = false },
                properties = DialogProperties(dismissOnBackPress = false)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(80.dp)
                )
            }
        }
    }

    LaunchedEffect(intent) {
        // Redirect to the channel if the intent has a channel URL
        appState.redirectChannelIfNeeded(intent) {
            navController.navigateToChannel(it)
        }
    }
}

class SendbirdUikitSampleAppState(context: Context) {
    val pref = SendbirdUikitPref(context)
    val isLoggedIn: Boolean
        get() = pref.userId.isNotEmpty()

    suspend fun saveAppId(appId: String) {
        pref.appId = appId
        delay(1000)
        Process.killProcess(Process.myPid())
    }

    suspend fun login(userId: String, nickname: String) {
        if (userId.isNotEmpty() && nickname.isNotEmpty()) {
            SendbirdUikitCompose.prepare(
                UikitCurrentUserInfo(userId = userId)
            )
            SendbirdUikitCompose.connect()
            SendbirdPushHelper.registerHandler(MyFirebaseMessagingService())
            pref.userId = userId
            SendbirdChat.awaitUpdateCurrentUserInfo(UserUpdateParams().apply {
                this.nickname = nickname
            })
        }
    }

    suspend fun logout() {
        runCatching {
            SendbirdPushHelper.awaitUnregisterHandler()
        }.onFailure {
            // ignore
        }

        runCatching {
            SendbirdUikitCompose.disconnect()
        }.onSuccess {
            pref.userId = ""
        }.onFailure {
            throw it
        }
    }

    fun redirectChannelIfNeeded(
        intent: Intent?,
        onNavigateToChannel: (channelUrl: String) -> Unit = {}
    ) {
        if (intent == null) return
        if (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) {
            intent.removeExtra(MyFirebaseMessagingService.PUSH_REDIRECT_CHANNEL)
            intent.removeExtra(MyFirebaseMessagingService.PUSH_REDIRECT_MESSAGE_ID)
        }
        if (intent.hasExtra(MyFirebaseMessagingService.PUSH_REDIRECT_CHANNEL)) {
            val channelUrl = intent.getStringExtra(MyFirebaseMessagingService.PUSH_REDIRECT_CHANNEL) ?: return
            onNavigateToChannel(channelUrl)
            intent.removeExtra(MyFirebaseMessagingService.PUSH_REDIRECT_CHANNEL)
        }
    }
}
