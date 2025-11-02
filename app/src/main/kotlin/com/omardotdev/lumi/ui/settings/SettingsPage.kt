/*
 * Lumi :3
 * Copyright (C) 2025 Omar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
*/

package com.omardotdev.lumi.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsPage() {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(16.dp, 32.dp, 16.dp, 16.dp)
    ) {
        Text(
            "Settings", fontSize = 24.sp,
            modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 16.dp)
        )

        Text(
            "Not implemented yet :(",
            fontSize = 16.sp
        )
    }
}