/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * Row layout follows SukiSU-Ultra's SettingsMiuix.kt (GPL-3.0): one card per group, a leading icon
 * on every row, and a popup dropdown rather than a dialog spinner.
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import top.youzix.neko.AppState
import top.youzix.neko.ui.AppIcons
import top.youzix.neko.ui.UiEngine
import top.youzix.neko.ui.miuix.ThemeModeOptions
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

/**
 * 设置 — everything that changes how the shell looks, and the way into everything that describes
 * it. The page owns no state: the rows read [AppState] and write it back through the prefs object,
 * which is what makes a change survive a restart.
 */
@Composable
fun SettingsScreen(
    contentPadding: PaddingValues,
    scrollBehavior: ScrollBehavior,
    colorSchemeMode: ColorSchemeMode,
    onColorSchemeModeChange: (ColorSchemeMode) -> Unit,
    useLiquidGlass: Boolean,
    onUseLiquidGlassChange: (Boolean) -> Unit,
    engine: UiEngine,
    onEngineChange: (UiEngine) -> Unit,
    onOpenAbout: () -> Unit,
    onOpenLicenses: () -> Unit,
) {
    val context = LocalContext.current
    val themeLabels = remember { ThemeModeOptions.map { it.second } }
    val engineLabels = remember { UiEngine.entries.map { it.label } }
    val selectedThemeIndex = ThemeModeOptions
        .indexOfFirst { it.first == colorSchemeMode }
        .coerceAtLeast(0)

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
                    OverlayDropdownPreference(
                        title = "主题模式",
                        summary = "浅色、深色或跟随系统，动态取色跟随壁纸",
                        items = themeLabels,
                        selectedIndex = selectedThemeIndex,
                        startAction = { Icon(AppIcons.Palette, null, Modifier.size(22.dp), colorScheme.onBackground) },
                        onSelectedIndexChange = { index ->
                            ThemeModeOptions.getOrNull(index)?.let { onColorSchemeModeChange(it.first) }
                        },
                    )
                    SwitchPreference(
                        title = "液态玻璃底栏",
                        summary = "底栏实时模糊与高光",
                        startAction = { Icon(AppIcons.BlurOn, null, Modifier.size(22.dp), colorScheme.onBackground) },
                        checked = useLiquidGlass,
                        onCheckedChange = onUseLiquidGlassChange,
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
