/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * The shell follows InstallerX-Revived's Material 3 layout (GPL-3.0): an outer Scaffold that owns
 * only the bottom navigation, with each page carrying its own large top app bar.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package top.youzix.neko.ui.material3

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.youzix.neko.AppState
import top.youzix.neko.crash.CrashHandler
import top.youzix.neko.demo.HomeTarget
import top.youzix.neko.ui.UiEnginePrefs
import top.youzix.neko.ui.material3.about.MaterialAboutScreen
import top.youzix.neko.ui.material3.gallery.MaterialGalleryScreen
import top.youzix.neko.ui.material3.home.MaterialHomeScreen
import top.youzix.neko.ui.material3.licenses.MaterialLicensesScreen
import top.youzix.neko.ui.material3.list.MaterialListScreen
import top.youzix.neko.ui.material3.settings.MaterialSettingsScreen
import top.youzix.neko.ui.material3.widgets.SwipeableSnackbarHost
import top.youzix.neko.ui.predictiveback.PredictiveBackHost
import top.youzix.neko.ui.rememberMainPagerState

private const val TAB_HOME = 0
private const val TAB_LIST = 1
private const val TAB_GALLERY = 2
private const val TAB_SETTINGS = 3

/**
 * Second-level pages. Only these take part in predictive back: switching between the bottom
 * navigation tabs moves between siblings, so there is no parent screen to preview.
 */
private enum class MaterialSubPage(val title: String) {
    About("关于"),
    Licenses("开源许可"),
}

@Composable
fun MaterialApp() {
    val context = LocalContext.current
    var themeMode by remember { mutableStateOf(UiEnginePrefs.loadMaterialTheme(context)) }
    var dynamicColor by remember { mutableStateOf(UiEnginePrefs.loadDynamicColor(context)) }
    val darkTheme = when (themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    MaterialAppTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
        MaterialShell(
            themeMode = themeMode,
            onThemeModeChange = {
                themeMode = it
                UiEnginePrefs.saveMaterialTheme(context, it)
            },
            dynamicColor = dynamicColor,
            onDynamicColorChange = {
                dynamicColor = it
                UiEnginePrefs.saveDynamicColor(context, it)
            },
        )
    }
}

@Composable
private fun MaterialShell(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
) {
    var subPage by remember { mutableStateOf<MaterialSubPage?>(null) }
    val context = LocalContext.current

    val labels = listOf("首页", "列表", "组件", "设置")
    // Filled icon while selected, outlined while not: SukiSU's bar switches between the two.
    val navItems = listOf(
        MaterialNavItem("首页", Icons.Filled.Home, Icons.Outlined.Home),
        MaterialNavItem("列表", Icons.AutoMirrored.Filled.List, Icons.AutoMirrored.Outlined.List),
        MaterialNavItem("组件", Icons.Filled.Widgets, Icons.Outlined.Widgets),
        MaterialNavItem("设置", Icons.Filled.Settings, Icons.Outlined.Settings),
    )

    val pagerState = rememberPagerState(pageCount = { labels.size })
    val mainPagerState = rememberMainPagerState(pagerState)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val notify: (String) -> Unit = { message ->
        coroutineScope.launch { snackbarHostState.showSnackbar(message) }
    }

    // Adopt the pager's position after the user swipes between tabs by hand.
    LaunchedEffect(pagerState.currentPage) {
        mainPagerState.syncPage()
        CrashHandler.note("页签 ${labels.getOrNull(pagerState.currentPage) ?: pagerState.currentPage}")
    }

    // ---- level-1 back: no predictive visual, just the tab-switch animation back to home ----
    BackHandler(enabled = subPage == null && mainPagerState.selectedPage != 0) {
        mainPagerState.animateToPage(0)
    }

    // ---- second-level pages ----
    // A level-two page owns its whole surface, app bar included, exactly as the four tabs do.
    Box(modifier = Modifier.fillMaxSize()) {
        PredictiveBackHost(
            subPageOpen = subPage != null,
            style = AppState.predictiveBackStyle,
            onDismissed = { subPage = null },
            levelOne = {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    bottomBar = {
                        // Driven by selectedPage, not the pager's currentPage, so the highlight
                        // lands the moment the tab is tapped.
                        MaterialBottomBar(
                            items = navItems,
                            selectedIndex = mainPagerState.selectedPage,
                            onSelect = { mainPagerState.animateToPage(it) },
                        )
                    },
                ) { outerPadding ->
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = true,
                    ) { page ->
                        when (page) {
                            TAB_HOME -> MaterialHomeScreen(
                                outerPadding = outerPadding,
                                useBlur = AppState.useBlur,
                                onOpenLink = { target ->
                                    when (target) {
                                        HomeTarget.Gallery -> mainPagerState.animateToPage(TAB_GALLERY)
                                        HomeTarget.Licenses -> subPage = MaterialSubPage.Licenses
                                    }
                                },
                            )

                            TAB_LIST -> MaterialListScreen(
                                outerPadding = outerPadding,
                                useBlur = AppState.useBlur,
                            )

                            TAB_GALLERY -> MaterialGalleryScreen(
                                outerPadding = outerPadding,
                                useBlur = AppState.useBlur,
                                onNotify = notify,
                            )

                            TAB_SETTINGS -> MaterialSettingsScreen(
                                outerPadding = outerPadding,
                                useBlur = AppState.useBlur,
                                themeMode = themeMode,
                                onThemeModeChange = onThemeModeChange,
                                dynamicColor = dynamicColor,
                                onDynamicColorChange = onDynamicColorChange,
                                useBlurEnabled = AppState.useBlur,
                                onUseBlurChange = {
                                    AppState.useBlur = it
                                    UiEnginePrefs.saveUseBlur(context, it)
                                },
                                engine = AppState.engine,
                                onEngineChange = {
                                    AppState.engine = it
                                    UiEnginePrefs.save(context, it)
                                },
                                onOpenAbout = { subPage = MaterialSubPage.About },
                                onOpenLicenses = { subPage = MaterialSubPage.Licenses },
                            )
                        }
                    }
                }
            },
            subPage = { closeSubPage ->
                LaunchedEffect(subPage) {
                    CrashHandler.note("二级页面 ${subPage?.title ?: ""}")
                }
                when (subPage) {
                    MaterialSubPage.About -> MaterialAboutScreen(
                        onBack = closeSubPage,
                        useBlur = AppState.useBlur,
                        onOpenLicenses = { subPage = MaterialSubPage.Licenses },
                        onNotify = notify,
                    )

                    MaterialSubPage.Licenses -> MaterialLicensesScreen(
                        onBack = closeSubPage,
                        useBlur = AppState.useBlur,
                    )

                    null -> Unit
                }
            },
        )

        // The host lives here, not inside the tab Scaffold: a second-level page is drawn over that
        // Scaffold, so a message posted from one would otherwise appear behind it.
        SwipeableSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = 88.dp +
                        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                ),
        )
    }
}
