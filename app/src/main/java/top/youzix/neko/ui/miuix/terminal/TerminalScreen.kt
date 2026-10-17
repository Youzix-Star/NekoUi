/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * 终端页：终端的外观，没有终端。
 *
 * 配色与提示符来自 termux-app（出处逐条写在 ui/terminal/ConsoleStyle.kt）；敲一条命令只会追加
 * 一行演示文本，App 里没有任何地方会去执行它。
 */

package top.youzix.neko.ui.miuix.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.youzix.neko.demo.Console
import top.youzix.neko.ui.terminal.ConsoleColors
import top.youzix.neko.ui.terminal.consoleLine
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

/** 终端的字号与行距。行距给足，` - ` 那种缩进行才看得出层次。 */
private val TerminalFont = 13.sp
private val TerminalLineHeight = 19.sp

/**
 * 终端.
 *
 * 上：整屏输出，自动滚到底。下：一行输入，回车或「执行」把它追加到输出里。
 */
@Composable
fun TerminalScreen(
    contentPadding: PaddingValues,
    scrollBehavior: ScrollBehavior,
) {
    val lines = Console.screen
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // 每接一行就跟到底：终端永远停在提示符那儿。
    LaunchedEffect(lines.size) {
        if (lines.isNotEmpty()) listState.scrollToItem(lines.lastIndex)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ConsoleColors.Background)
            .padding(contentPadding),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            itemsIndexed(lines) { index, line ->
                Text(
                    text = consoleLine(line),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = TerminalFont,
                        lineHeight = TerminalLineHeight,
                        color = ConsoleColors.Foreground,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (index == lines.lastIndex) 2.dp else 0.dp),
                )
            }
        }

        InputRow(
            value = input,
            onValueChange = { input = it },
            onSubmit = {
                Console.submit(input)
                input = ""
            },
        )
    }
}

/**
 * 输入行。
 *
 * 用的是主题里的输入框（它是 miuix 的组件，外观跟随主题），因为纯黑底上加一条边框比
 * 「在提示符后面插光标」要清楚得多 —— 后者在手机上没法点、也没法改。
 */
@Composable
private fun InputRow(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = "命令",
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        Button(onClick = onSubmit, enabled = value.isNotBlank()) {
            Text("执行", style = MiuixTheme.textStyles.button)
        }
    }
}
