/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * The theme page follows SukiSU-Ultra's ColorPaletteScreenMaterial.kt (GPL-3.0): colour mode,
 * palette style, colour spec, a seed colour and the blur switch.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package top.youzix.neko.ui.material3.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import top.youzix.neko.AppState
import top.youzix.neko.ui.AppIcons
import top.youzix.neko.ui.UiEnginePrefs
import top.youzix.neko.ui.material3.components.SegmentedColumn
import top.youzix.neko.ui.material3.components.SegmentedDropdownItem
import top.youzix.neko.ui.material3.components.SegmentedSwitchItem
import top.youzix.neko.ui.material3.material3AppBarColor
import top.youzix.neko.ui.material3.material3BlurEffect
import top.youzix.neko.ui.material3.rememberMaterial3BlurBackdrop
import top.youzix.neko.ui.theme.ColorMode
import top.youzix.neko.ui.theme.ColorSpecs
import top.youzix.neko.ui.theme.PaletteStyles
import top.youzix.neko.ui.theme.SeedPresets
import top.yukonga.miuix.kmp.blur.layerBackdrop

/**
 * 主题 — SukiSU's colour palette page, Material flavour.
 */
@Composable
fun MaterialThemeScreen(onBack: () -> Unit, useBlur: Boolean) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val backdrop = rememberMaterial3BlurBackdrop(useBlur)
    val modeLabels = remember { ColorMode.entries.map { it.label } }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopAppBar(
                modifier = Modifier.material3BlurEffect(backdrop),
                title = { Text("主题", modifier = Modifier.padding(start = 12.dp)) },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                    }
                },
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
            contentPadding = paddingValues + PaddingValues(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            item {
                SegmentedColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "模式",
                ) {
                    item {
                        SegmentedDropdownItem(
                            icon = AppIcons.Palette,
                            title = "主题模式",
                            summary = "浅色、深色，或从壁纸/种子色取色",
                            items = modeLabels,
                            selectedIndex = ColorMode.entries.indexOf(AppState.colorMode).coerceAtLeast(0),
                            onItemSelected = { index ->
                                ColorMode.entries.getOrNull(index)?.let {
                                    AppState.colorMode = it
                                    UiEnginePrefs.saveColorMode(context, it)
                                }
                            },
                        )
                    }
                }
            }

            item {
                SegmentedColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "颜色",
                ) {
                    item {
                        SegmentedDropdownItem(
                            icon = AppIcons.Gallery,
                            title = "配色风格",
                            summary = "取色时使用的调色板",
                            items = PaletteStyles,
                            selectedIndex = PaletteStyles.indexOf(AppState.paletteStyle).coerceAtLeast(0),
                            onItemSelected = { index ->
                                PaletteStyles.getOrNull(index)?.let {
                                    AppState.paletteStyle = it
                                    UiEnginePrefs.savePaletteStyle(context, it)
                                }
                            },
                        )
                    }
                    item {
                        SegmentedDropdownItem(
                            icon = AppIcons.Tune,
                            title = "颜色规格",
                            summary = "2021 或 2025 的取色算法",
                            items = ColorSpecs,
                            selectedIndex = ColorSpecs.indexOf(AppState.colorSpec).coerceAtLeast(0),
                            onItemSelected = { index ->
                                ColorSpecs.getOrNull(index)?.let {
                                    AppState.colorSpec = it
                                    UiEnginePrefs.saveColorSpec(context, it)
                                }
                            },
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "种子色",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        SeedSwatch(
                            selected = AppState.keyColor == 0,
                            color = MaterialTheme.colorScheme.primary,
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

            item {
                SegmentedColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "界面",
                ) {
                    item {
                        SegmentedSwitchItem(
                            icon = AppIcons.BlurOn,
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
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        if (selected) {
            Icon(
                imageVector = AppIcons.Grant,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}
