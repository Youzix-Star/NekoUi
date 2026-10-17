/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * Row layout follows SukiSU-Ultra's SettingsMaterial.kt (GPL-3.0): segmented groups, one leading
 * icon per row, and a chevron on the rows that open a second-level page.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package top.youzix.neko.ui.material3.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import top.youzix.neko.AppState
import top.youzix.neko.ui.AppIcons
import top.youzix.neko.ui.UiEngine
import top.youzix.neko.ui.material3.ThemeMode
import top.youzix.neko.ui.material3.components.IconTile
import top.youzix.neko.ui.material3.components.SegmentedColumn
import top.youzix.neko.ui.material3.components.SegmentedDropdownItem
import top.youzix.neko.ui.material3.components.SegmentedListItem
import top.youzix.neko.ui.material3.components.SegmentedSwitchItem
import top.youzix.neko.ui.material3.material3AppBarColor
import top.youzix.neko.ui.material3.material3BlurEffect
import top.youzix.neko.ui.material3.rememberMaterial3BlurBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop

/**
 * 设置 — everything that changes how the shell looks, and the way into everything that describes
 * it. The page owns no state: the rows read [AppState] and write it back through the prefs object,
 * which is what makes a change survive a restart.
 */
@Composable
fun MaterialSettingsScreen(
    outerPadding: PaddingValues,
    useBlur: Boolean,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    useBlurEnabled: Boolean,
    onUseBlurChange: (Boolean) -> Unit,
    engine: UiEngine,
    onEngineChange: (UiEngine) -> Unit,
    onOpenAbout: () -> Unit,
    onOpenLicenses: () -> Unit,
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val backdrop = rememberMaterial3BlurBackdrop(useBlur)

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeFlexibleTopAppBar(
                modifier = Modifier.material3BlurEffect(backdrop),
                title = { Text("设置", modifier = Modifier.padding(start = 12.dp)) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backdrop.material3AppBarColor(),
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    scrolledContainerColor = backdrop.material3AppBarColor(),
                ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(backdrop?.let { Modifier.layerBackdrop(it) } ?: Modifier)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues + outerPadding),
        ) {
            SegmentedColumn(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 13.dp),
                title = "外观",
            ) {
                item {
                    SegmentedDropdownItem(
                        icon = AppIcons.Palette,
                        title = "主题模式",
                        summary = "浅色、深色或跟随系统",
                        items = ThemeMode.entries.map { it.label },
                        selectedIndex = ThemeMode.entries.indexOf(themeMode).coerceAtLeast(0),
                        onItemSelected = { index ->
                            ThemeMode.entries.getOrNull(index)?.let(onThemeModeChange)
                        },
                    )
                }
                item {
                    SegmentedSwitchItem(
                        icon = AppIcons.Tune,
                        title = "动态取色",
                        summary = "跟随壁纸取色",
                        checked = dynamicColor,
                        onCheckedChange = onDynamicColorChange,
                    )
                }
                item {
                    SegmentedSwitchItem(
                        icon = AppIcons.BlurOn,
                        title = "顶栏模糊",
                        summary = "滚动时顶栏的半透明模糊",
                        checked = useBlurEnabled,
                        onCheckedChange = onUseBlurChange,
                    )
                }
            }

            SegmentedColumn(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 13.dp),
                title = "引擎",
            ) {
                item {
                    SegmentedDropdownItem(
                        icon = AppIcons.Gallery,
                        title = "界面引擎",
                        summary = "切换整套界面实现",
                        items = UiEngine.entries.map { it.label },
                        selectedIndex = UiEngine.entries.indexOf(engine).coerceAtLeast(0),
                        onItemSelected = { index ->
                            UiEngine.entries.getOrNull(index)?.let(onEngineChange)
                        },
                    )
                }
            }

            SegmentedColumn(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 13.dp),
                title = "关于",
            ) {
                item {
                    SegmentedListItem(
                        onClick = onOpenAbout,
                        headlineContent = { Text("关于 NekoUi") },
                        supportingContent = { Text("版本、源码与设备信息") },
                        leadingContent = { IconTile(AppIcons.About, "关于 NekoUi") },
                        trailingContent = { Icon(AppIcons.ChevronRight, null) },
                    )
                }
                item {
                    SegmentedListItem(
                        onClick = onOpenLicenses,
                        headlineContent = { Text("开源许可") },
                        supportingContent = { Text("依赖的许可证") },
                        leadingContent = { IconTile(AppIcons.License, "开源许可") },
                        trailingContent = { Icon(AppIcons.ChevronRight, null) },
                    )
                }
            }
        }
    }
}
