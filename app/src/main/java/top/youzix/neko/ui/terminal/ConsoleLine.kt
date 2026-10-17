/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * 一行终端文字怎么上样式。放在共享层，因为它跟引擎无关：两套引擎画的是同一块画面，
 * 差别只在外面套什么。
 */

package top.youzix.neko.ui.terminal

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

/**
 * One line of console output.
 *
 * The rules are Termux's: on a prompt line the path is green and bold while the command keeps the
 * default colour; a line naming the app is bold; links are underlined; everything else is left
 * exactly as written, indentation included.
 */
fun consoleLine(line: String): AnnotatedString = buildAnnotatedString {
    if (line.startsWith(PromptText)) {
        withStyle(SpanStyle(color = ConsoleColors.DimGreen, fontWeight = FontWeight.Bold)) {
            append(PromptPath)
        }
        append(line.removePrefix(PromptPath))
        return@buildAnnotatedString
    }

    val link = line.indexOf("https://")
    if (link >= 0) {
        append(line.substring(0, link))
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
            append(line.substring(link))
        }
        return@buildAnnotatedString
    }

    if (line.startsWith("NekoUi")) {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(line) }
        return@buildAnnotatedString
    }

    append(line)
}
