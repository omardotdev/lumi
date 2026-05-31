/*
 * Lumi
 * Copyright (C) 2026 Omar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
*/
package com.omardotdev.lumi

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.omardotdev.lumi.navigation.Navigator
import com.omardotdev.lumi.navigation.rememberNavigationState
import com.omardotdev.lumi.navigation.toEntries
import com.omardotdev.lumi.ui.about.AboutPage
import com.omardotdev.lumi.ui.home.HomePage
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {
    @Serializable
    data object Home : NavKey
    @Serializable
    data object About : NavKey
}

enum class Destination(
    val route: NavKey,
    val label: String,
    val icon: Int,
    val contentDescription: String
) {
    HOME(Screen.Home, "Home", R.drawable.ic_home, "Home"),
    ABOUT(Screen.About, "About", R.drawable.ic_info, "About"),
}

@Composable
fun App() {
    val routes = setOf(Screen.Home, Screen.About)
    val navigationState = rememberNavigationState(
        startRoute = Screen.Home,
        topLevelRoutes = routes
    )
    val navigator = remember { Navigator(navigationState) }
    val entryProvider = entryProvider {
        entry<Screen.Home> {
            HomePage()
        }

        entry<Screen.About> {
            AboutPage()
        }
    }

    var selectedDestination = navigationState.backStacks[navigationState.topLevelRoute]?.last()

    Scaffold(
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = selectedDestination == destination.route,
                        onClick = {
                            navigator.navigate(destination.route)
                            selectedDestination = destination.route
                        },
                        icon = {
                            Icon(
                                painterResource(destination.icon),
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        NavDisplay(
            modifier = Modifier.padding(contentPadding),
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() },
            sceneStrategies = remember { listOf(DialogSceneStrategy()) }
        )
    }
}