/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * Rows are built from the segmented-column widgets ported from InstallerX-Revived (GPL-3.0).
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package top.youzix.neko.ui.material3.about

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import top.youzix.neko.AppState
import top.youzix.neko.BuildConfig
import top.youzix.neko.ui.AppIcons
import top.youzix.neko.ui.AppMark
import top.youzix.neko.ui.UiEngine
import top.youzix.neko.ui.UiEnginePrefs
import top.youzix.neko.ui.material3.ThemeMode
import top.youzix.neko.ui.material3.material3AppBarColor
import top.youzix.neko.ui.material3.material3BlurEffect
import top.youzix.neko.ui.material3.rememberMaterial3BlurBackdrop
import top.youzix.neko.ui.material3.widgets.DropDownMenuWidget
import top.youzix.neko.ui.material3.widgets.NavigationItemWidget
import top.youzix.neko.ui.material3.widgets.SegmentedColumn
import top.youzix.neko.ui.material3.widgets.SwitchWidget
import top.youzix.neko.ui.predictiveback.PredictiveBackStyle
import top.youzix.neko.util.DeviceInfo
import top.yukonga.miuix.kmp.blur.layerBackdrop

private const val REPOSITORY_URL = "https://github.com/Youzix-Star/NekoUi"

/**
 * The merged settings and about page: how the app looks, which engine draws it, and what it is
 * built on.
 */
@Composable
fun MaterialAboutScreen(
    outerPadding: PaddingValues,
    useBlur: Boolean,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    onOpenLicenses: () -> Unit,
    onNotify: (String) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    // Held as the string itself: null means there is nothing to show, so the dialog needs no
    // second Boolean to say whether it is open.
    var deviceInfo by remember { mutableStateOf<String?>(null) }
    // Read above the list, not inside it: the groups are declared by a non-composable DSL lambda,
    // so a value read down there would not be what brings this page back when it changes.
    val engine = AppState.engine
    val predictiveBackStyle = AppState.predictiveBackStyle
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
                title = { Text("关于", modifier = Modifier.padding(start = 12.dp)) },
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
            item { AppHeader() }

            item {
                SegmentedColumn(title = "外观") {
                    item {
                        DropDownMenuWidget(
                            title = "主题模式",
                            choice = ThemeMode.entries.indexOf(themeMode).coerceAtLeast(0),
                            data = ThemeMode.entries.map { it.label },
                            onChoiceChange = { index ->
                                ThemeMode.entries.getOrNull(index)?.let(onThemeModeChange)
                            },
                        )
                    }
                    item {
                        SwitchWidget(
                            title = "动态取色",
                            description = "跟随壁纸取色",
                            checked = dynamicColor,
                            onCheckedChange = onDynamicColorChange,
                        )
                    }
                    item {
                        DropDownMenuWidget(
                            icon = AppIcons.Back,
                            title = "预见式返回动画",
                            description = "二级页面返回时的跟手动画",
                            choice = PredictiveBackStyle.entries
                                .indexOf(predictiveBackStyle).coerceAtLeast(0),
                            data = PredictiveBackStyle.entries.map { it.label },
                            onChoiceChange = { index ->
                                PredictiveBackStyle.entries.getOrNull(index)?.let {
                                    AppState.predictiveBackStyle = it
                                    UiEnginePrefs.savePredictiveBackStyle(context, it)
                                }
                            },
                        )
                    }
                }
            }

            item {
                SegmentedColumn(title = "引擎") {
                    item {
                        DropDownMenuWidget(
                            title = "界面引擎",
                            description = "切换整套界面实现",
                            choice = UiEngine.entries.indexOf(engine).coerceAtLeast(0),
                            data = UiEngine.entries.map { it.label },
                            onChoiceChange = { index ->
                                UiEngine.entries.getOrNull(index)?.let {
                                    AppState.engine = it
                                    UiEnginePrefs.save(context, it)
                                }
                            },
                        )
                    }
                }
            }

            item {
                SegmentedColumn(title = "关于") {
                    item {
                        NavigationItemWidget(
                            icon = AppIcons.SourceCode,
                            title = "获取源代码",
                            description = "GitHub 上的源码",
                            onClick = { uriHandler.openUri(REPOSITORY_URL) },
                        )
                    }
                    item {
                        NavigationItemWidget(
                            icon = AppIcons.License,
                            title = "开源许可",
                            description = "依赖的许可证",
                            onClick = onOpenLicenses,
                        )
                    }
                    item {
                        // The snapshot is taken at tap time, never during composition: the
                        // snapshot time inside the text only means something if it is the moment
                        // the row was pressed. Setting it is also what opens the dialog.
                        NavigationItemWidget(
                            icon = AppIcons.Phones,
                            title = "设备信息",
                            description = "机型、系统与显示",
                            onClick = { deviceInfo = DeviceInfo.snapshot(context) },
                        )
                    }
                }
            }
        }
    }

    val info = deviceInfo
    if (info != null) {
        AlertDialog(
            onDismissRequest = { deviceInfo = null },
            title = { Text("设备信息") },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        text = info,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(info))
                        onNotify("已复制设备信息")
                        deviceInfo = null
                    },
                ) {
                    Text("复制")
                }
            },
            dismissButton = {
                TextButton(onClick = { deviceInfo = null }) { Text("关闭") }
            },
        )
    }
}

@Composable
private fun AppHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // The app mark. Its size is left to the design language's own, so it stays a mark and not
        // a banner; the column centres it.
        AppMark(
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 6.dp),
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(text = "NekoUi", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "只有界面，没有别的。",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
