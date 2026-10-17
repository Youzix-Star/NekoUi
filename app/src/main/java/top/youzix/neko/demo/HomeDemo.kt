/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.demo

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import top.youzix.neko.AppState
import top.youzix.neko.BuildConfig
import top.youzix.neko.ui.AppIcons

/** How a block on the home page is coloured. */
enum class Tone { OK, WARN, BAD, BUSY, IDLE, ACCENT }

/** The main status block: one headline, one sentence of detail, one line of hint. */
data class Status(
    val tone: Tone,
    val headline: String,
    val detail: String,
    val hint: String,
)

/** A tag shown inside the status block. */
data class Tag(val label: String, val tone: Tone)

/** A one-line card under the status block. */
data class InfoRow(
    val id: String,
    val title: String,
    val summary: String,
    val hint: String,
    val active: Boolean,
    val icon: ImageVector,
)

/** One cell of the statistics row. */
data class Stat(val title: String, val value: String)

/** A row of the overview card. */
data class Overview(
    val title: String,
    val summary: String,
    val icon: ImageVector,
)

/**
 * Everything the home page draws.
 *
 * This is the page's whole input, and it is written here rather than fetched: the shell has
 * nothing behind it to ask. Both engines read this one value, which is what keeps the two home
 * pages showing the same thing.
 */
data class Home(
    val icon: ImageVector,
    val status: Status,
    val tags: List<Tag>,
    val rows: List<InfoRow>,
    val stats: List<Stat>,
    val overview: List<Overview>,
)

/** The home page's content. Reads [AppState], so a settings change redraws it. */
@Composable
fun demoHome(): Home = Home(
    icon = AppIcons.Home,
    status = Status(
        tone = Tone.OK,
        headline = "界面外壳",
        detail = "页面骨架、两套引擎、崩溃兜底都在；背后没有业务逻辑。",
        hint = "在「设置」里切换引擎、主题与返回动画。",
    ),
    tags = listOf(
        Tag("双引擎", Tone.ACCENT),
        Tag("已签名", Tone.OK),
        Tag("v${BuildConfig.VERSION_NAME}", Tone.IDLE),
    ),
    rows = listOf(
        InfoRow(
            id = "engine",
            title = "界面引擎",
            summary = AppState.engine.label,
            hint = "设置 → 引擎",
            active = true,
            icon = AppIcons.Tune,
        ),
        InfoRow(
            id = "demo",
            title = "演示数据",
            summary = "四个页签都是占位内容",
            hint = "无副作用",
            active = true,
            icon = AppIcons.Grant,
        ),
    ),
    stats = listOf(
        Stat("页签", "4"),
        Stat("引擎", "2"),
        Stat("控件", "24"),
    ),
    overview = listOf(
        Overview("应用版本", BuildConfig.VERSION_NAME, AppIcons.About),
        Overview("系统版本", "Android ${Build.VERSION.RELEASE}", AppIcons.Phones),
        Overview("界面引擎", AppState.engine.label, AppIcons.Tune),
        Overview("崩溃处理", "独立进程崩溃页", AppIcons.Debug),
    ),
)
