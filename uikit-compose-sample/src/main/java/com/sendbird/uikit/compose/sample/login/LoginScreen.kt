package com.sendbird.uikit.compose.sample.login

import android.view.KeyEvent.ACTION_DOWN
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sendbird.uikit.compose.component.SendbirdContainedButton
import com.sendbird.uikit.compose.sample.R
import com.sendbird.uikit.compose.theme.SendbirdFontFamily
import com.sendbird.uikit.compose.theme.SendbirdOpacity
import com.sendbird.uikit.compose.theme.SendbirdTheme

const val LoginRoute = "login_route"

/**
 * A composable function to display the login screen.
 * @param modifier Modifier
 * @param defaultAppId The default app ID.
 * @param onSaveAppId A lambda function to save the app ID.
 * @param onLogin A lambda function to login.
 */
@Composable
fun LoginScreen(
    defaultAppId: String,
    modifier: Modifier = Modifier,
    onSaveAppId: (appId: String) -> Unit = {},
    onLogin: (userId: String, nickname: String) -> Unit = { _, _ -> }
) {
    val focusManager = LocalFocusManager.current
    var appId: String by remember {
        mutableStateOf(defaultAppId)
    }
    var userId: String by remember {
        mutableStateOf("")
    }
    var nickname: String by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 48.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(id = R.drawable.logo_sendbird),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(bottom = 48.dp),
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextField(
                    modifier = Modifier
                        .weight(3f)
                        .padding(end = 8.dp)
                        .onPreviewKeyEvent {
                            if (it.key == Key.Tab && it.nativeKeyEvent.action == ACTION_DOWN) {
                                focusManager.moveFocus(FocusDirection.Down)
                                true
                            } else {
                                false
                            }
                        },
                    value = appId,
                    onValueChange = { appId = it },
                    label = {
                        Text(
                            text = stringResource(id = R.string.text_label_app_id),
                            fontFamily = SendbirdFontFamily.Roboto
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
                SendbirdContainedButton(modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                    text = stringResource(id = R.string.text_button_save),
                    onClick = { onSaveAppId(appId) }
                )
            }
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .onPreviewKeyEvent {
                        if (it.key == Key.Tab && it.nativeKeyEvent.action == ACTION_DOWN) {
                            focusManager.moveFocus(FocusDirection.Down)
                            true
                        } else {
                            false
                        }
                    },
                value = userId,
                onValueChange = { userId = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.text_label_user_id),
                        fontFamily = SendbirdFontFamily.Roboto
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .onPreviewKeyEvent {
                        if (it.key == Key.Tab && it.nativeKeyEvent.action == ACTION_DOWN) {
                            focusManager.moveFocus(FocusDirection.Down)
                            true
                        } else {
                            false
                        }
                    },
                value = nickname,
                onValueChange = { nickname = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.text_label_user_nickname),
                        fontFamily = SendbirdFontFamily.Roboto
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            SendbirdContainedButton(modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
                text = stringResource(id = R.string.text_button_sign_in),
                onClick = { onLogin(userId, nickname) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.text_version_info,
                    com.sendbird.uikit.compose.BuildConfig.VERSION_NAME,
                    com.sendbird.android.BuildConfig.VERSION_NAME),
                color = MaterialTheme.colorScheme.onBackground.copy(SendbirdOpacity.MediumOpacity),
                style = MaterialTheme.typography.labelMedium)
            Image(painter = painterResource(id = R.drawable.logo_sendbird_full), contentDescription = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    SendbirdTheme {
        LoginScreen("appId")
    }
}
