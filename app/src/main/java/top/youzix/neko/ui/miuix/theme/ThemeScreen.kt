/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * The theme page follows SukiSU-Ultra's ColorPaletteScreenMiuix.kt (GPL-3.0): colour mode, palette
 * style, colour spec, a seed colour and the blur switch, all in the same order.
 */

package top.youzix.neko.ui.miuix.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import top.youzix.neko.AppState
import top.youzix.neko.ui.AppIcons
import top.youzix.neko.ui.UiEnginePrefs
import top.youzix.neko.ui.theme.ColorMode
import top.youzix.neko.ui.theme.ColorSpecs
import top.youzix.neko.ui.theme.PaletteStyles
import top.youzix.neko.ui.theme.SeedPresets
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

/**
 * 主题 — SukiSU's colour palette page, ported to this shell.
 *
 * Every row writes straight into [AppState] and the prefs object; the theme wrappers read the same
 * values, so a change repaints both engines immediately.
 */
@Composable
fun ThemeScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scrollBehavior = MiuixScrollBehavior()
    val modeLabels = remember { ColorMode.entries.map { it.label } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "主题",
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = AppIcons.Back, contentDescription = "返回")
                    }
                },
            )
        },
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        val contentPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(layoutDirection),
            top = innerPadding.calculateTopPadding(),
            end = innerPadding.calculateEndPadding(layoutDirection),
            bottom = innerPadding.calculateBottomPadding() + 24.dp,
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .overScrollVertical(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "mode") {
                Column {
                    SmallTitle(text = "模式")
                    Card(modifier = Modifier.fillMaxWidth()) {
                        OverlayDropdownPreference(
                            title = "主题模式",
                            summary = "浅色、深色，或从壁纸/种子色取色",
                            items = modeLabels,
                            selectedIndex = ColorMode.entries.indexOf(AppState.colorMode).coerceAtLeast(0),
                            startAction = { Icon(AppIcons.Palette, null, Modifier.size(22.dp)) },
                            onSelectedIndexChange = { index ->
                                ColorMode.entries.getOrNull(index)?.let {
                                    AppState.colorMode = it
                                    UiEnginePrefs.saveColorMode(context, it)
                                }
                            },
                        )
                    }
                }
            }

            item(key = "colour") {
                Column {
                    SmallTitle(text = "颜色")
                    Card(modifier = Modifier.fillMaxWidth()) {
                        OverlayDropdownPreference(
                            title = "配色风格",
                            summary = "取色时使用的调色板",
                            items = PaletteStyles,
                            selectedIndex = PaletteStyles.indexOf(AppState.paletteStyle).coerceAtLeast(0),
                            startAction = { Icon(AppIcons.Gallery, null, Modifier.size(22.dp)) },
                            onSelectedIndexChange = { index ->
                                PaletteStyles.getOrNull(index)?.let {
                                    AppState.paletteStyle = it
                                    UiEnginePrefs.savePaletteStyle(context, it)
                                }
                            },
                        )
                        OverlayDropdownPreference(
                            title = "颜色规格",
                            summary = "2021 或 2025 的取色算法",
                            items = ColorSpecs,
                            selectedIndex = ColorSpecs.indexOf(AppState.colorSpec).coerceAtLeast(0),
                            startAction = { Icon(AppIcons.Tune, null, Modifier.size(22.dp)) },
                            onSelectedIndexChange = { index ->
                                ColorSpecs.getOrNull(index)?.let {
                                    AppState.colorSpec = it
                                    UiEnginePrefs.saveColorSpec(context, it)
                                }
                            },
                        )
                    }
                }
            }

            item(key = "seed") {
                Column {
                    SmallTitle(text = "种子色")
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = if (AppState.keyColor == 0) "跟随壁纸" else "自定义种子色",
                                style = MiuixTheme.textStyles.footnote1,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                SeedSwatch(
                                    selected = AppState.keyColor == 0,
                                    color = MiuixTheme.colorScheme.primary,
                                    label = "自动",
                                    onClick = {
                                        AppState.keyColor = 0
                                        UiEnginePrefs.saveKeyColor(context, 0)
                                    },
                                )
                                SeedPresets.forEach { argb ->
                                    SeedSwatch(
                                        selected = AppState.keyColor == argb,
                                        color = Color(argb),
                                        label = null,
                                        onClick = {
                                            AppState.keyColor = argb
                                            UiEnginePrefs.saveKeyColor(context, argb)
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item(key = "blur") {
                Column {
                    SmallTitle(text = "界面")
                    Card(modifier = Modifier.fillMaxWidth()) {
                        SwitchPreference(
                            title = "模糊与液态玻璃",
                            summary = "底栏与顶栏的半透明模糊",
                            checked = AppState.useBlur,
                            onCheckedChange = {
                                AppState.useBlur = it
                                UiEnginePrefs.saveUseBlur(context, it)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeedSwatch(
    selected: Boolean,
    color: Color,
    label: String?,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MiuixTheme.textStyles.footnote2,
                color = MiuixTheme.colorScheme.onPrimary,
            )
        }
        if (selected) {
            Icon(
                imageVector = AppIcons.Grant,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MiuixTheme.colorScheme.onPrimary,
            )
        }
    }
}
