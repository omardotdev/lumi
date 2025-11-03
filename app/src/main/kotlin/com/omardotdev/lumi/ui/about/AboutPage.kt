/*
 * Lumi :3
 * Copyright (C) 2025 Omar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
*/

package com.omardotdev.lumi.ui.about

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omardotdev.lumi.BuildConfig

@Composable
fun AboutPage() {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(16.dp, 32.dp, 16.dp, 16.dp)
    ) {
        Text(
            "About", fontSize = 24.sp,
            modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 16.dp)
        )

        val supportsDynamicColors: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        Text(
            "Lumi, the cutest cat client for Minky :3\n",
            fontSize = 16.sp
        )

        Text(
            "Version: ${BuildConfig.VERSION}\nAndroid Version/API Level: ${Build.VERSION.SDK_INT}\nSupports Dynamic colors: $supportsDynamicColors\nMade by Omar (omardotdev)",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}