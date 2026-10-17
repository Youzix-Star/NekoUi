/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.ui.miuix.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import top.youzix.neko.ui.miuix.ThemeModeOptions
import top.youzix.neko.ui.predictiveback.PredictiveBackStyle
import top.youzix.neko.util.DeviceInfo
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.preference.WindowSpinnerPreference
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

private const val REPOSITORY_URL = "https://github.com/Youzix-Star/NekoUi"

/**
 * 关于 — the settings page and the about page in one.
 *
 * Everything that configures the shell (appearance, engine) sits above everything that only
 * describes it, because this is the only place either of them can live.
 */
@Composable
fun AboutScreen(
    contentPadding: PaddingValues,
    scrollBehavior: ScrollBehavior,
    colorSchemeMode: ColorSchemeMode,
    onColorSchemeModeChange: (ColorSchemeMode) -> Unit,
    useLiquidGlass: Boolean,
    onUseLiquidGlassChange: (Boolean) -> Unit,
    engine: UiEngine,
    onEngineChange: (UiEngine) -> Unit,
    onOpenLicenses: () -> Unit,
    onNotify: (String) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    // Held as the string itself: null means there is nothing to show, so the dialog needs no
    // second Boolean to say whether it is open.
    var deviceInfo by remember { mutableStateOf<String?>(null) }

    val themeItems = remember { ThemeModeOptions.map { DropdownItem(text = it.second) } }
    val engineItems = remember { UiEngine.entries.map { DropdownItem(text = it.label) } }
    val backStyleItems = remember { PredictiveBackStyle.entries.map { DropdownItem(text = it.label) } }
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
        item(key = "header") {
            AppHeader()
        }

        item(key = "appearance") {
            Column {
                SmallTitle(text = "外观")
                Card(modifier = Modifier.fillMaxWidth()) {
                    WindowSpinnerPreference(
                        title = "主题模式",
                        items = themeItems,
                        selectedIndex = selectedThemeIndex,
                        onSelectedIndexChange = { index ->
                            ThemeModeOptions.getOrNull(index)?.let { onColorSchemeModeChange(it.first) }
                        },
                    )
                    WindowSpinnerPreference(
                        title = "预见式返回动画",
                        summary = "二级页面的返回跟手动画",
                        items = backStyleItems,
                        selectedIndex = PredictiveBackStyle.entries
                            .indexOf(AppState.predictiveBackStyle).coerceAtLeast(0),
                        onSelectedIndexChange = { index ->
                            PredictiveBackStyle.entries.getOrNull(index)?.let {
                                AppState.predictiveBackStyle = it
                                UiEnginePrefs.savePredictiveBackStyle(context, it)
                            }
                        },
                    )
                    SwitchPreference(
                        title = "液态玻璃底栏",
                        summary = "底栏实时模糊与高光",
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
                    WindowSpinnerPreference(
                        title = "界面引擎",
                        summary = "切换整套界面实现",
                        items = engineItems,
                        selectedIndex = UiEngine.entries.indexOf(engine).coerceAtLeast(0),
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
                        title = "获取源代码",
                        summary = "GitHub 上的源码",
                        startAction = {
                            Icon(
                                imageVector = AppIcons.SourceCode,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                            )
                        },
                        onClick = { uriHandler.openUri(REPOSITORY_URL) },
                    )
                    ArrowPreference(
                        title = "开源许可",
                        summary = "依赖的许可证",
                        startAction = {
                            Icon(
                                imageVector = AppIcons.License,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                            )
                        },
                        onClick = onOpenLicenses,
                    )
                    ArrowPreference(
                        title = "设备信息",
                        summary = "机型、系统与显示",
                        startAction = {
                            Icon(
                                imageVector = AppIcons.Phones,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                            )
                        },
                        // Read on tap, not in composition: the snapshot time is only useful if it
                        // is the time of the snapshot.
                        onClick = { deviceInfo = DeviceInfo.snapshot(context) },
                    )
                }
            }
        }
    }

    deviceInfo?.let { info ->
        OverlayDialog(
            show = true,
            title = "设备信息",
            onDismissRequest = { deviceInfo = null },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        text = info,
                        style = MiuixTheme.textStyles.footnote2,
                        fontFamily = FontFamily.Monospace,
                    )
                }
                Button(
                    onClick = {
                        clipboard.setText(AnnotatedString(info))
                        onNotify("已复制设备信息")
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("复制")
                }
                Button(onClick = { deviceInfo = null }, modifier = Modifier.fillMaxWidth()) {
                    Text("关闭")
                }
            }
        }
    }
}

/** The brand block: the mark, the name, the version. */
@Composable
private fun AppHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppMark(
            color = MiuixTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 6.dp),
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "NekoUi",
            style = MiuixTheme.textStyles.title1,
            color = MiuixTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "只有界面，没有别的。",
            style = MiuixTheme.textStyles.footnote2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}
