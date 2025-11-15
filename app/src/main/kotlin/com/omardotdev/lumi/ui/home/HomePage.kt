/*
 * Lumi :3
 * Copyright (C) 2025 Omar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
*/

package com.omardotdev.lumi.ui.home

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import java.io.File
import androidx.core.net.toUri
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.omardotdev.lumi.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomePage() {
    val sizeResolver = rememberConstraintsSizeResolver()
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data("https://minky.materii.dev")
            .diskCachePolicy(CachePolicy.DISABLED)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .size(sizeResolver)
            .build()
    )

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(16.dp, 32.dp, 16.dp, 16.dp)
    ) {
        Text(
            "Home",
            fontSize = 24.sp,
            modifier = Modifier
                .padding(0.dp, 16.dp, 0.dp, 16.dp)
        )

        Surface(
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .then(sizeResolver)
                    .aspectRatio(1f)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp)
                    )
                    .clip(RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            )
        }


        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(0.dp, 8.dp, 0.dp, 8.dp)
        ) {
            val ctx = LocalContext.current
            val permissionsDialog = remember { mutableStateOf(false) }
            val higherThanOrRedVelvetCake = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
            val hasPermission = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permissionsDialog.value) PermissionDialog(permissionsDialog)

            FilledTonalButton(onClick = { painter.restart() }) {
                Text(stringResource(R.string.refresh))
            }

            FilledTonalButton(
                onClick = {
                    if (hasPermission.status.isGranted && !higherThanOrRedVelvetCake || !hasPermission.status.isGranted && higherThanOrRedVelvetCake) {
                        downloadImage(ctx)
                    } else {
                        permissionsDialog.value = true
                    }
                }
            ) {
                Text(stringResource(R.string.download))
            }
        }

        Text(
            stringResource(R.string.home_text),
            fontSize = 16.sp
        )
    }
}

private fun downloadImage(context: Context) {
    try {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri: Uri = "https://minky.materii.dev".toUri()
        val request = DownloadManager.Request(downloadUri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle("minky")
            .setMimeType("image/jpeg")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES,
                File.separator + "minky.jpg"
            )
        dm.enqueue(request)
    } catch (e: Exception) {
        Log.d("Lumi", "Failed to download image :(", e)
    }
}

@Composable
fun PermissionDialog(shouldShowDialog: MutableState<Boolean>) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) shouldShowDialog.value = false
    }

    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, contentDescription = "Info")
        },

        title = {
            Text(stringResource(R.string.no_permissions))
        },

        text = {
            Text(stringResource(R.string.grant_permission))
        },

        onDismissRequest = {
            shouldShowDialog.value = false
        },

        confirmButton = {
            Button(
                onClick = {
                    launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            ) {
                Text(stringResource(R.string.grant))
            }
        },

        dismissButton = {
            FilledTonalButton(
                onClick = {
                    shouldShowDialog.value = false
                }
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}
