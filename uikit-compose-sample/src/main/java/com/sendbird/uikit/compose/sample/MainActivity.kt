package com.sendbird.uikit.compose.sample

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.sendbird.uikit.compose.theme.SecondaryMain
import com.sendbird.uikit.compose.theme.SendbirdTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    private val appSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SendbirdTheme {
                SendbirdUikitApp(intent)
            }
        }
        checkPostNotificationPermission()
    }

    private fun checkPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    permission
                ) == PermissionChecker.PERMISSION_GRANTED
            ) {
                return
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, permission)) {
                showPermissionRationalePopup()
                return
            }
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun showPermissionRationalePopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.text_dialog_permission_title))
        builder.setMessage(
            String.format(
                Locale.US,
                getString(R.string.text_need_to_allow_permission_notification),
                applicationInfo.loadLabel(packageManager).toString()
            )
        )
        builder.setPositiveButton(R.string.text_go_to_settings) { _: DialogInterface?, _: Int ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:$packageName")
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            appSettingLauncher.launch(intent)
        }
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(SecondaryMain.toArgb())
    }
}
