package com.song.bookshelf.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.song.bookshelf.feature.scan.ScanPlaceholderScreen
import com.song.bookshelf.feature.search.SearchScreen
import com.song.bookshelf.feature.settings.SettingsScreen
import com.song.bookshelf.feature.shelf.ShelfScreen

object Routes {
    const val SHELF = "shelf"
    const val SEARCH = "search"
    const val SETTINGS = "settings"
    const val SCAN = "scan"
}

private enum class TopLevelDestination(val route: String, val label: String, val icon: ImageVector) {
    SHELF(Routes.SHELF, "책장", Icons.Filled.Book),
    SEARCH(Routes.SEARCH, "검색", Icons.Filled.Search),
    SETTINGS(Routes.SETTINGS, "설정", Icons.Filled.Settings),
}

@Composable
fun BookshelfAppUi() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    // 스캔 화면은 카메라 전체 화면이라 하단 탭과 스캔 버튼을 숨긴다.
    val showChrome = currentDestination?.route != Routes.SCAN

    Scaffold(
        // 인셋은 각 화면의 TopAppBar 와 NavigationBar 가 직접 처리한다.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showChrome) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { dest ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = null) },
                            label = { Text(dest.label) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showChrome) {
                FloatingActionButton(onClick = { navController.navigate(Routes.SCAN) }) {
                    Icon(Icons.Filled.QrCodeScanner, contentDescription = "바코드 스캔")
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SHELF,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            composable(Routes.SHELF) { ShelfScreen() }
            composable(Routes.SEARCH) { SearchScreen() }
            composable(Routes.SETTINGS) { SettingsScreen() }
            composable(Routes.SCAN) { ScanPlaceholderScreen(onBack = { navController.popBackStack() }) }
        }
    }
}
