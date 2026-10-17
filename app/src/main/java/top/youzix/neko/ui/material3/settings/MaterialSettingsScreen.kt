/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * Row layout follows SukiSU-Ultra's SettingsMaterial.kt (GPL-3.0): appearance is one row into the
 * theme page, then the engine, then the identity rows.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package top.youzix.neko.ui.material3.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import top.youzix.neko.ui.AppIcons
import top.youzix.neko.ui.UiEngine
import top.youzix.neko.ui.material3.components.IconTile
import top.youzix.neko.ui.material3.components.SegmentedColumn
import top.youzix.neko.ui.material3.components.SegmentedDropdownItem
import top.youzix.neko.ui.material3.components.SegmentedListItem
import top.youzix.neko.ui.material3.material3AppBarColor
import top.youzix.neko.ui.material3.material3BlurEffect
import top.youzix.neko.ui.material3.rememberMaterial3BlurBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop

@Composable
fun MaterialSettingsScreen(
    outerPadding: PaddingValues,
    useBlur: Boolean,
    engine: UiEngine,
    onEngineChange: (UiEngine) -> Unit,
    onOpenTheme: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenLicenses: () -> Unit,
) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .then(backdrop?.let { Modifier.layerBackdrop(it) } ?: Modifier),
            contentPadding = paddingValues + outerPadding,
        ) {
            item {
                SegmentedColumn(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    title = "外观",
                ) {
                    item {
                        SegmentedListItem(
                            onClick = onOpenTheme,
                            headlineContent = { Text("主题") },
                            supportingContent = { Text("配色、种子色、模糊") },
                            leadingContent = { IconTile(AppIcons.Palette, "主题") },
                            trailingContent = { Icon(AppIcons.ChevronRight, null) },
                        )
                    }
                }
            }

            item {
                SegmentedColumn(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
            }

            item {
                SegmentedColumn(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
}
