/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package top.youzix.neko.ui.material3.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.youzix.neko.demo.DocsDemo
import top.youzix.neko.demo.LogDemo
import top.youzix.neko.ui.material3.components.StatusTag
import top.youzix.neko.ui.material3.components.TonalCard
import top.youzix.neko.ui.material3.material3AppBarColor
import top.youzix.neko.ui.material3.material3BlurEffect
import top.youzix.neko.ui.material3.rememberMaterial3BlurBackdrop
import top.youzix.neko.ui.material3.widgets.SwitchWidget
import top.yukonga.miuix.kmp.blur.layerBackdrop

/**
 * 组件 — every control the shell offers, on one page.
 *
 * This is the page an AI (or a person) should scroll through before writing a new screen: if what
 * it needs is here, it should be reused rather than rebuilt.
 */
@Composable
fun MaterialGalleryScreen(
    outerPadding: PaddingValues,
    useBlur: Boolean,
    onNotify: (String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val backdrop = rememberMaterial3BlurBackdrop(useBlur)
    var switchOn by remember { mutableStateOf(true) }
    var sliderValue by remember { mutableStateOf(0.4f) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeFlexibleTopAppBar(
                modifier = Modifier.material3BlurEffect(backdrop),
                title = { Text("组件", modifier = Modifier.padding(start = 12.dp)) },
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
            contentPadding = PaddingValues(16.dp) + paddingValues + outerPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "text") {
                Section(title = "文本") {
                    Text("标题", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "正文：界面只有两件事要交代清楚，现在是什么状态、接下来能做什么。",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "脚注：其余都是装饰。",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item(key = "buttons") {
                Section(title = "按钮") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = { onNotify("主要操作") }) { Text("主要操作") }
                        FilledTonalButton(onClick = { onNotify("次要操作") }) { Text("次要") }
                        TextButton(onClick = {}, enabled = false) { Text("不可用") }
                    }
                }
            }

            item(key = "tags") {
                Section(title = "标签") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusTag(
                            label = "稳定",
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        StatusTag(
                            label = "实验",
                            backgroundColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        StatusTag(
                            label = "强调",
                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        StatusTag(
                            label = "默认",
                            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item(key = "controls") {
                Section(title = "开关与滑杆") {
                    SwitchWidget(
                        title = "开关",
                        description = "打开或关闭一项行为",
                        checked = switchOn,
                        onCheckedChange = { switchOn = it },
                    )
                    Slider(value = sliderValue, onValueChange = { sliderValue = it })
                    Text(
                        text = "滑杆 ${(sliderValue * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item(key = "card") {
                Section(title = "卡片") {
                    TonalCard(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        onClick = { onNotify("点了卡片") },
                        onLongClick = { onNotify("长按了卡片") },
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("可点的卡片", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "点一下有反馈，长按也有。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            )
                        }
                    }
                }
            }

            item(key = "palette") {
                Section(title = "调色板") {
                    PaletteRow("primary", MaterialTheme.colorScheme.primary)
                    PaletteRow("secondary", MaterialTheme.colorScheme.secondary)
                    PaletteRow("tertiary", MaterialTheme.colorScheme.tertiary)
                    PaletteRow("error", MaterialTheme.colorScheme.error)
                    PaletteRow("surface", MaterialTheme.colorScheme.surface)
                    PaletteRow("surfaceContainer", MaterialTheme.colorScheme.surfaceContainerHigh)
                }
            }

            item(key = "code") {
                Section(title = "代码块") {
                    Text(
                        text = DocsDemo.CODE,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            item(key = "log") {
                Section(title = "日志") {
                    LogDemo.lines.takeLast(4).forEach { line ->
                        Text(
                            text = line,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item(key = "doc") {
                Section(title = "文档") {
                    Text(DocsDemo.TITLE, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    DocsDemo.paragraphs.forEach { paragraph ->
                        Text(paragraph, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(6.dp))
                    DocsDemo.bulletPoints.forEach { point ->
                        Text("· $point", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "「${DocsDemo.QUOTE}」",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            item(key = "dialog") {
                Section(title = "对话框与提示") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = { showDialog = true }) { Text("打开对话框") }
                        Button(onClick = { onNotify("这是一条提示") }) { Text("显示提示") }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("对话框") },
            text = { Text("打断是有代价的：能用一个二级页解决的问题，不要用对话框。") },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("知道了") }
            },
        )
    }
}

/** A titled card: the gallery's unit of structure. */
@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )
        TonalCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Column(modifier = Modifier.padding(16.dp)) { content() }
        }
    }
}

/** One labelled colour swatch. */
@Composable
private fun PaletteRow(label: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(color, RoundedCornerShape(6.dp)),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 10.dp),
        )
    }
}
