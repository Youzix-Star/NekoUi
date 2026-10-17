/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * 终端那一屏的外观：配色、提示符、行内样式。
 *
 * 配色取自 termux-app 的 terminal-emulator/TerminalColorScheme.java 默认色表（纯黑底、纯白字），
 * 因为它就是「手机上的终端」长什么样。这里是外观，不是终端：没有 PTY，也没有真在跑的 shell。
 */

package top.youzix.neko.ui.terminal

import androidx.compose.ui.graphics.Color

/** The palette the console panel is drawn with. */
object ConsoleColors {
    val DimGreen = Color(0xFF00CD00)
    val DimYellow = Color(0xFFCDCD00)
    val Foreground = Color(0xFFFFFFFF)
    val Background = Color(0xFF000000)
    val Cursor = Color(0xFFFFFFFF)
}

/** 提示符：绿色路径 + 白色 `$` + 一个空格。 */
const val PromptPath = "~"
const val PromptText = "~ $ "
