/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.ui.miuix

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.youzix.neko.AppState
import top.youzix.neko.crash.CrashHandler
import top.youzix.neko.ui.AppIcons
import top.youzix.neko.ui.UiEngine
import top.youzix.neko.ui.UiEnginePrefs
import top.youzix.neko.ui.miuix.about.AboutScreen
import top.youzix.neko.ui.miuix.gallery.GalleryScreen
import top.youzix.neko.ui.miuix.home.HomeScreen
import top.youzix.neko.ui.miuix.licenses.LicensesScreen
import top.youzix.neko.ui.miuix.list.ListScreen
import top.youzix.neko.ui.miuix.liquid.FloatingBottomBar
import top.youzix.neko.ui.miuix.settings.SettingsScreen
import top.youzix.neko.ui.predictiveback.PredictiveBackHost
import top.youzix.neko.ui.rememberMainPagerState
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

const val TAB_HOME = 0
const val TAB_LIST = 1
const val TAB_GALLERY = 2
const val TAB_SETTINGS = 3

/**
 * Second-level pages.
 *
 * Only these respond to a back gesture: a tab switch moves between siblings, so the platform
 * transition (which previews the *parent* you are returning to) has no meaning there.
 */
enum class MiuixSubPage(val title: String) {
    About("关于"),
    Licenses("开源许可"),
}

/** The miuix engine: large-title app bar, a pager of tabs, and the liquid-glass bottom bar. */
@Composable
fun MiuixApp() {
    val context = LocalContext.current
    var colorSchemeMode by remember { mutableStateOf(UiEnginePrefs.loadMiuixTheme(context)) }

    // One persisted switch drives translucency in both engines: this engine's liquid-glass
    // bottom bar, and the Material 3 engine's blurred top bar.
    val useLiquidGlass = AppState.useBlur

    MiuixAppTheme(colorSchemeMode = colorSchemeMode) {
        MiuixShell(
            colorSchemeMode = colorSchemeMode,
            onColorSchemeModeChange = {
                colorSchemeMode = it
                UiEnginePrefs.saveMiuixTheme(context, it)
            },
            useLiquidGlass = useLiquidGlass,
            onUseLiquidGlassChange = {
                AppState.useBlur = it
                UiEnginePrefs.saveUseBlur(context, it)
            },
            engine = AppState.engine,
            onEngineChange = {
                AppState.engine = it
                UiEnginePrefs.save(context, it)
            },
        )
    }
}

@Composable
fun MiuixShell(
    colorSchemeMode: ColorSchemeMode,
    onColorSchemeModeChange: (ColorSchemeMode) -> Unit,
    useLiquidGlass: Boolean,
    onUseLiquidGlassChange: (Boolean) -> Unit,
    engine: UiEngine,
    onEngineChange: (UiEngine) -> Unit,
) {
    var subPage by remember { mutableStateOf<MiuixSubPage?>(null) }

    val navigationItems = remember {
        listOf(
            NavigationItem(label = "首页", icon = AppIcons.Home),
            NavigationItem(label = "列表", icon = AppIcons.List),
            NavigationItem(label = "组件", icon = AppIcons.Gallery),
            NavigationItem(label = "设置", icon = AppIcons.Settings),
        )
    }
    val titles = remember { listOf("NekoUi", "列表", "组件", "设置") }

    val pagerState = rememberPagerState(pageCount = { navigationItems.size })
    val mainPagerState = rememberMainPagerState(pagerState)
    val mainScrollBehavior = MiuixScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val notify: (String) -> Unit = { message ->
        coroutineScope.launch { snackbarHostState.showSnackbar(message) }
    }

    val surfaceColor = MiuixTheme.colorScheme.surface
    val backdrop = rememberLayerBackdrop {
        drawRect(surfaceColor)
        drawContent()
    }

    // Adopt the pager's position after the user swipes between tabs by hand.
    LaunchedEffect(pagerState.currentPage) {
        mainPagerState.syncPage()
        CrashHandler.note("页签 ${titles.getOrNull(pagerState.currentPage) ?: pagerState.currentPage}")
    }

    // ---- level-1 back: no predictive visual, just the tab-switch animation back to home ----
    BackHandler(enabled = subPage == null && mainPagerState.selectedPage != 0) {
        mainPagerState.animateToPage(0)
    }

    // ---- second-level pages ----
    // The gesture, its animation and the two stacked layers all live in PredictiveBackHost, which
    // both engines share. The shell only says what the level-one content and the page are.
    Box(modifier = Modifier.fillMaxSize()) {
        PredictiveBackHost(
            subPageOpen = subPage != null,
            style = AppState.predictiveBackStyle,
            onDismissed = { subPage = null },
            levelOne = {
                MiuixTabs(
                    titles = titles,
                    navigationItems = navigationItems,
                    pagerState = pagerState,
                    selectedPage = mainPagerState.selectedPage,
                    scrollBehavior = mainScrollBehavior,
                    backdrop = backdrop,
                    useLiquidGlass = useLiquidGlass,
                    colorSchemeMode = colorSchemeMode,
                    onColorSchemeModeChange = onColorSchemeModeChange,
                    useLiquidGlassChange = onUseLiquidGlassChange,
                    engine = engine,
                    onEngineChange = onEngineChange,
                    onNotify = notify,
                    onOpenAbout = { subPage = MiuixSubPage.About },
                    onOpenLicenses = { subPage = MiuixSubPage.Licenses },
                    onTabSelected = { index -> mainPagerState.animateToPage(index) },
                )
            },
            subPage = { closeSubPage ->
                val page = subPage
                if (page != null) {
                    // One scroll behaviour, handed to the app bar *and* to the page below it: giving
                    // it to the page alone is what stops a page from scrolling.
                    val pageScrollBehavior = MiuixScrollBehavior()
                    LaunchedEffect(page) { CrashHandler.note("二级页面 ${page.title}") }
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = page.title,
                                scrollBehavior = pageScrollBehavior,
                                navigationIcon = {
                                    IconButton(onClick = closeSubPage) {
                                        Icon(
                                            imageVector = AppIcons.Back,
                                            contentDescription = "返回",
                                        )
                                    }
                                },
                            )
                        },
                    ) { innerPadding ->
                        val layoutDirection = LocalLayoutDirection.current
                        val subPadding = PaddingValues(
                            start = innerPadding.calculateStartPadding(layoutDirection) + 12.dp,
                            top = innerPadding.calculateTopPadding() + 12.dp,
                            end = innerPadding.calculateEndPadding(layoutDirection) + 12.dp,
                            bottom = innerPadding.calculateBottomPadding() + 24.dp,
                        )
                        when (page) {
                            MiuixSubPage.About -> AboutScreen(
                                contentPadding = subPadding,
                                scrollBehavior = pageScrollBehavior,
                                onOpenLicenses = { subPage = MiuixSubPage.Licenses },
                                onNotify = notify,
                            )

                            MiuixSubPage.Licenses -> LicensesScreen(contentPadding = subPadding)
                        }
                    }
                }
            },
        )

        // The host lives here, not inside the tab Scaffold: a second-level page is drawn over that
        // Scaffold, so a message posted from one would otherwise appear behind it.
        SnackbarHost(
            state = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = 84.dp +
                        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                ),
        )
    }
}

/** Level one: the four tabs. Switching between them is a pager animation, nothing more. */
@Composable
private fun MiuixTabs(
    titles: List<String>,
    navigationItems: List<NavigationItem>,
    pagerState: PagerState,
    selectedPage: Int,
    scrollBehavior: ScrollBehavior,
    backdrop: LayerBackdrop,
    useLiquidGlass: Boolean,
    colorSchemeMode: ColorSchemeMode,
    onColorSchemeModeChange: (ColorSchemeMode) -> Unit,
    useLiquidGlassChange: (Boolean) -> Unit,
    engine: UiEngine,
    onEngineChange: (UiEngine) -> Unit,
    onNotify: (String) -> Unit,
    onOpenAbout: () -> Unit,
    onOpenLicenses: () -> Unit,
    onTabSelected: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                // Follows the requested tab, not the pager, so the title flips together
                // with the bottom-bar highlight the instant a tab is tapped.
                title = titles[selectedPage],
                largeTitle = titles[selectedPage],
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            // The bar belongs to the bottom edge of the screen, keyboard or no keyboard: it
            // consumes the IME inset here so that nothing above it can lift it out of place.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .consumeWindowInsets(WindowInsets.ime),
            ) {
                FloatingBottomBar(
                    items = navigationItems,
                    selectedIndex = selectedPage,
                    onItemClick = onTabSelected,
                    backdrop = backdrop,
                    isBlurActive = useLiquidGlass,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            bottom = 12.dp +
                                WindowInsets.navigationBars
                                    .asPaddingValues()
                                    .calculateBottomPadding(),
                        ),
                )
            }
        },
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        val pagePadding = PaddingValues(
            start = innerPadding.calculateStartPadding(layoutDirection) + 12.dp,
            top = innerPadding.calculateTopPadding() + 12.dp,
            end = innerPadding.calculateEndPadding(layoutDirection) + 12.dp,
            bottom = innerPadding.calculateBottomPadding() + 12.dp,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                // The glass bottom bar samples this layer.
                .layerBackdrop(backdrop),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true,
            ) { page ->
                when (page) {
                    TAB_HOME -> HomeScreen(
                        contentPadding = pagePadding,
                        scrollBehavior = scrollBehavior,
                    )

                    TAB_LIST -> ListScreen(
                        contentPadding = pagePadding,
                        scrollBehavior = scrollBehavior,
                    )

                    TAB_GALLERY -> GalleryScreen(
                        contentPadding = pagePadding,
                        scrollBehavior = scrollBehavior,
                        onNotify = onNotify,
                    )

                    else -> SettingsScreen(
                        contentPadding = pagePadding,
                        scrollBehavior = scrollBehavior,
                        colorSchemeMode = colorSchemeMode,
                        onColorSchemeModeChange = onColorSchemeModeChange,
                        useLiquidGlass = useLiquidGlass,
                        onUseLiquidGlassChange = useLiquidGlassChange,
                        engine = engine,
                        onEngineChange = onEngineChange,
                        onOpenAbout = onOpenAbout,
                        onOpenLicenses = onOpenLicenses,
                    )
                }
            }
        }
    }
}
