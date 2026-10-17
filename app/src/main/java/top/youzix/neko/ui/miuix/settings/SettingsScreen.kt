/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * Row layout follows SukiSU-Ultra's SettingsMiuix.kt (GPL-3.0): appearance is one row into the
 * theme page, then the engine, then the identity rows.
 */

package top.youzix.neko.ui.miuix.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import top.youzix.neko.ui.AppIcons
import top.youzix.neko.ui.UiEngine
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

@Composable
fun SettingsScreen(
    contentPadding: PaddingValues,
    scrollBehavior: ScrollBehavior,
    engine: UiEngine,
    onEngineChange: (UiEngine) -> Unit,
    onOpenTheme: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenLicenses: () -> Unit,
) {
    val engineLabels = remember { UiEngine.entries.map { it.label } }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .overScrollVertical(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "appearance") {
            Column {
                SmallTitle(text = "外观")
                Card(modifier = Modifier.fillMaxWidth()) {
                    ArrowPreference(
                        title = "主题",
                        summary = "配色、种子色、模糊",
                        startAction = { Icon(AppIcons.Palette, null, Modifier.size(22.dp), colorScheme.onBackground) },
                        onClick = onOpenTheme,
                    )
                }
            }
        }

        item(key = "engine") {
            Column {
                SmallTitle(text = "引擎")
                Card(modifier = Modifier.fillMaxWidth()) {
                    OverlayDropdownPreference(
                        title = "界面引擎",
                        summary = "切换整套界面实现",
                        items = engineLabels,
                        selectedIndex = UiEngine.entries.indexOf(engine).coerceAtLeast(0),
                        startAction = { Icon(AppIcons.Gallery, null, Modifier.size(22.dp), colorScheme.onBackground) },
                        onSelectedIndexChange = { index ->
                            UiEngine.entries.getOrNull(index)?.let(onEngineChange)
                        },
                    )
                }
            }
        }

        item(key = "about") {
            Column {
                SmallTitle(text = "关于")
                Card(modifier = Modifier.fillMaxWidth()) {
                    ArrowPreference(
                        title = "关于 NekoUi",
                        summary = "版本、源码与设备信息",
                        startAction = { Icon(AppIcons.About, null, Modifier.size(22.dp), colorScheme.onBackground) },
                        onClick = onOpenAbout,
                    )
                    ArrowPreference(
                        title = "开源许可",
                        summary = "依赖的许可证",
                        startAction = { Icon(AppIcons.License, null, Modifier.size(22.dp), colorScheme.onBackground) },
                        onClick = onOpenLicenses,
                    )
                }
            }
        }
    }
}
