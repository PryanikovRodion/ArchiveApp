package com.pryanikov.archiveapp.view.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun RequestPermissions(
    onPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current

    val permissionsToRequest = remember {
        mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+
                add(Manifest.permission.READ_MEDIA_IMAGES)
                add(Manifest.permission.READ_MEDIA_VIDEO)
            } else {

                add(Manifest.permission.READ_EXTERNAL_STORAGE)

            }
        }.toTypedArray()
    }

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            val allGranted = perms.values.all { it }
            if (allGranted) {
                onPermissionsGranted()
            } else {
                Toast.makeText(context, "Немає всіх дозволив", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        multiplePermissionResultLauncher.launch(permissionsToRequest)
    }
}