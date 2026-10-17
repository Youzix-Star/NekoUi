/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * Material 3 引擎下的终端页：外面是这一套引擎自己的大标题栏，里面那块画面与行为跟 miuix
 * 完全一致 —— 外观样式在 ui/terminal 下共享，输出共用一个演示列表。
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package top.youzix.neko.ui.material3.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.youzix.neko.demo.Console
import top.youzix.neko.ui.material3.material3AppBarColor
import top.youzix.neko.ui.material3.material3BlurEffect
import top.youzix.neko.ui.material3.rememberMaterial3BlurBackdrop
import top.youzix.neko.ui.terminal.ConsoleColors
import top.youzix.neko.ui.terminal.consoleLine
import top.yukonga.miuix.kmp.blur.layerBackdrop

private val TerminalFont = 13.sp
private val TerminalLineHeight = 19.sp

/**
 * 终端.
 *
 * 面板是纯黑的，不跟随主题 —— 终端就该是终端的样子。顶栏仍然属于这个引擎。
 */
@Composable
fun MaterialTerminalScreen(
    outerPadding: PaddingValues,
    useBlur: Boolean,
) {
    val backdrop = rememberMaterial3BlurBackdrop(useBlur)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val lines = Console.screen
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(lines.size) {
        if (lines.isNotEmpty()) listState.scrollToItem(lines.lastIndex)
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeFlexibleTopAppBar(
                modifier = Modifier.material3BlurEffect(backdrop),
                title = { Text("终端", modifier = Modifier.padding(start = 12.dp)) },
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
                .padding(PaddingValues(0.dp) + paddingValues + outerPadding),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(ConsoleColors.Background)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                itemsIndexed(lines) { _, line ->
                    Text(
                        text = consoleLine(line),
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = TerminalFont,
                            lineHeight = TerminalLineHeight,
                            color = ConsoleColors.Foreground,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("命令") },
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                )
                Button(
                    onClick = {
                        Console.submit(input)
                        input = ""
                    },
                    enabled = input.isNotBlank(),
                ) {
                    Text("执行")
                }
            }
        }
    }
}
