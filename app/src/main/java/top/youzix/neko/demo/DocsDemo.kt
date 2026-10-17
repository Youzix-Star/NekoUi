/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.demo

/**
 * The text the gallery's document block renders.
 *
 * A handful of Markdown constructs, shown as styling rather than parsed: the gallery is a
 * catalogue of looks, not a Markdown viewer.
 */
object DocsDemo {

    const val TITLE = "组件规范"

    val paragraphs: List<String> = listOf(
        "界面只有两件事要交代清楚：现在是什么状态，接下来能做什么。其余都是装饰。",
    )

    val bulletPoints: List<String> = listOf(
        "一屏一个主操作；次要操作收进行尾或长按。",
        "状态用颜色和标签表达，不要用整段文字解释。",
        "空态要说出下一步，而不是只说「没有数据」。",
    )

    const val QUOTE = "能用一个二级页解决的，不要用对话框。"

    const val CODE = "val home = demoHome()\nHomeScreen(contentPadding, scrollBehavior)"

    val codeNote = "示例代码块：等宽字体 + 主题色背景，不长于屏幕一半。"
}
