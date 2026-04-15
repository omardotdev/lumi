/*
 * Lumi
 * Copyright (C) 2026 Omar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
*/

package com.omardotdev.lumi.ui.home

import android.Manifest
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.AsyncImagePainter
import coil3.compose.ImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.toBitmap
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.omardotdev.lumi.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp
                )
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(0.dp, 8.dp, 0.dp, 8.dp)
            ) {
                val ctx = LocalContext.current
                val permissionsDialog = remember { mutableStateOf(false) }
                val higherThanOrRedVelvetCake = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                val hasPermission =
                    rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                if (permissionsDialog.value) PermissionDialog(permissionsDialog)

                FilledTonalButton(onClick = { painter.restart() }) {
                    Text(stringResource(R.string.refresh))
                }

                FilledTonalButton(
                    onClick = {
                        if (hasPermission.status.isGranted && !higherThanOrRedVelvetCake || !hasPermission.status.isGranted && higherThanOrRedVelvetCake) {
                            downloadImage(ctx, painter)
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
}

private fun downloadImage(context: Context, painter: AsyncImagePainter) {
    val imageState = painter.state.value

    if (imageState is AsyncImagePainter.State.Success) {
        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "Minky.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Minky")
            }

            val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            imageUri?.let { uri ->
                context.contentResolver.openOutputStream(uri).use { stream ->
                    if (stream != null) {
                        imageState.result.image.toBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        Toast.makeText(context, "Minky saved to Pictures folder", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Lumi", "Failed to download image :(", e)
        }
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
