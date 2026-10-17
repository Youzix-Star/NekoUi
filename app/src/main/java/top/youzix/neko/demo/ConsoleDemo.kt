/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.demo

import androidx.compose.runtime.mutableStateListOf
import top.youzix.neko.ui.terminal.PromptText

/**
 * The terminal tab's lines.
 *
 * A shell look with no shell behind it: [submit] appends the command and one line saying nothing
 * ran. Both engines read the same list, so the two terminals stay in step.
 */
object Console {

    /** Every line on screen, oldest first. A snapshot list, so both engines observe it. */
    val screen = mutableStateListOf<String>().apply { addAll(welcome()) }

    /** Appends [command] the way a shell would echo it, then the demo reply. */
    fun submit(command: String) {
        val line = command.trim()
        if (line.isEmpty()) return
        screen += PromptText + line
        screen += "（演示外壳：这里不会执行任何命令）"
        screen += ""
        screen += PromptText
    }

    private fun welcome(): List<String> = listOf(
        "NekoUi ${top.youzix.neko.BuildConfig.VERSION_NAME}",
        "",
        "这是一个界面外壳：终端只有外观，没有 shell。",
        "输入任何内容都只会回一行演示文本。",
        "",
        PromptText,
    )
}
