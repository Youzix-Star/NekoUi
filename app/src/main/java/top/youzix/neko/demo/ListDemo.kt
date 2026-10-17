/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.demo

/** What a list row's tag is saying, in the few tones a list actually needs. */
enum class TagTone { ACCENT, OK, WARN, NEUTRAL }

/** One row of the sample list. */
data class ListItem(
    val id: String,
    val title: String,
    val summary: String,
    val tag: String? = null,
    val tone: TagTone = TagTone.NEUTRAL,
    val detail: String = "",
)

/** A titled group of rows: the list page's unit of structure. */
data class ListGroup(val title: String, val items: List<ListItem>)

/**
 * The list page's content.
 *
 * Written here rather than fetched: the point of the page is the shape of a searchable, grouped
 * list, not what is in it.
 */
object ListDemo {

    val groups: List<ListGroup> = listOf(
        ListGroup(
            title = "组件",
            items = listOf(
                ListItem(
                    id = "text",
                    title = "文本与标题",
                    summary = "六级标题与脚注，跟随主题的字号阶梯",
                    tag = "稳定",
                    tone = TagTone.OK,
                    detail = "标题、正文、脚注各有一套样式，两套引擎各用各的排版尺度。",
                ),
                ListItem(
                    id = "button",
                    title = "按钮",
                    summary = "主操作、次操作与纯文字按钮",
                    tag = "稳定",
                    tone = TagTone.OK,
                    detail = "主操作一屏只放一个 —— 放两个就没有主次了。",
                ),
                ListItem(
                    id = "tag",
                    title = "状态标签",
                    summary = "列表行尾的小色块，用来表达状态",
                    tag = "稳定",
                    tone = TagTone.OK,
                    detail = "标签只表达状态，不表达操作；操作放在行尾的按钮或长按菜单里。",
                ),
                ListItem(
                    id = "dialog",
                    title = "对话框",
                    summary = "确认、选择与全屏表单",
                    tag = "实验",
                    tone = TagTone.WARN,
                    detail = "对话框是打断，能不用就不用；能用二级页解决的表单不要塞进对话框。",
                ),
                ListItem(
                    id = "search",
                    title = "搜索栏",
                    summary = "顶部常驻输入框，带清空与空态",
                    tag = "实验",
                    tone = TagTone.WARN,
                    detail = "搜索是过滤，不是请求；本地过滤不要让用户等。",
                ),
            ),
        ),
        ListGroup(
            title = "页面",
            items = listOf(
                ListItem("home", "首页", "状态卡、统计与概览", "已实现", TagTone.OK),
                ListItem("list", "列表", "就是这一页", "已实现", TagTone.OK),
                ListItem("gallery", "组件", "所有控件一页看全", "已实现", TagTone.OK),
                ListItem("settings", "设置", "外观、引擎与入口", "已实现", TagTone.OK),
            ),
        ),
        ListGroup(
            title = "构建",
            items = listOf(
                ListItem("ci", "最近一次构建", "GitHub Actions · assembleRelease", "成功", TagTone.OK),
                ListItem("sign", "签名", "v2 + v3，同一把 release key", "已签", TagTone.ACCENT),
                ListItem("sdk", "目标 SDK", "compileSdk 37 / targetSdk 35 / minSdk 33", null),
            ),
        ),
    )

    /** Groups whose rows match [query], with empty groups dropped. An empty query matches all. */
    fun search(query: String): List<ListGroup> {
        val needle = query.trim()
        if (needle.isEmpty()) return groups
        return groups.mapNotNull { group ->
            val hits = group.items.filter {
                it.title.contains(needle, ignoreCase = true) ||
                    it.summary.contains(needle, ignoreCase = true) ||
                    it.tag?.contains(needle, ignoreCase = true) == true
            }
            if (hits.isEmpty()) null else group.copy(items = hits)
        }
    }

    /** How many rows the whole list has, for the empty state's "0 / N". */
    val total: Int = groups.sumOf { it.items.size }
}
