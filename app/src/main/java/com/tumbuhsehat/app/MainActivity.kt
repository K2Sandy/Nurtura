package com.tumbuhsehat.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tumbuhsehat.app.ui.navigation.TumbuhSehatApp
import com.tumbuhsehat.app.ui.theme.TumbuhSehatTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op: reminders still get created either way, they just won't show a system notification */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            TumbuhSehatTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TumbuhSehatApp()
                }
            }
        }
    }
}
