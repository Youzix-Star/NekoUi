/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * The page follows DSHA's home page (AGPL-3.0, same author): a coloured main status block, two
 * compact rows, a three-cell statistics row, then the overview list. Its card padding values are
 * copied deliberately — miuix's `Card` has `insideMargin = 0` by default, so a bare `Text` in a
 * card touches the edge unless the card asks for padding itself.
 */

package top.youzix.neko.ui.miuix.home

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import top.youzix.neko.demo.Home
import top.youzix.neko.demo.Overview
import top.youzix.neko.demo.InfoRow
import top.youzix.neko.demo.Stat
import top.youzix.neko.demo.Tone
import top.youzix.neko.demo.demoHome
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

/**
 * 首页 — the shell's front page.
 *
 * Everything on it comes from [demoHome]: the page has no state of its own and nothing behind it
 * to ask, which is what makes it the same page in both engines.
 */
@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    scrollBehavior: ScrollBehavior,
) {
    val home = demoHome()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .overScrollVertical(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "status") {
            StatusCard(home = home)
        }

        items(home.rows) { row ->
            CompactStatusCard(row = row)
        }

        item(key = "stats") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                home.stats.forEach { stat ->
                    StatisticCard(stat, modifier = Modifier.weight(1f))
                }
            }
        }

        item(key = "overview") {
            Column {
                SmallTitle(text = "概览")
                // 卡内是 BasicComponent：它自带 16dp 内边距，外层再给就会双重留白，所以这里不设。
                Card(modifier = Modifier.fillMaxWidth()) {
                    home.overview.forEach { entry ->
                        OverviewRow(entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewRow(entry: Overview) {
    BasicComponent(
        title = entry.title,
        summary = entry.summary,
        startAction = { Icon(entry.icon, null, Modifier.size(22.dp)) },
    )
}

/**
 * The main block: a panel coloured by state, with the icon of what it is describing as a large
 * faint watermark. Padding is explicit (20dp) because a miuix `Card` has none of its own.
 */
@Composable
private fun StatusCard(home: Home) {
    val (container, content) = colorsFor(home.status.tone)

    Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        colors = CardDefaults.defaultColors(color = container, contentColor = content),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = home.icon,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(88.dp)
                    .alpha(0.16f),
                tint = content,
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = home.status.headline,
                    color = content,
                    style = MiuixTheme.textStyles.title4,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = home.status.detail,
                    color = content,
                    style = MiuixTheme.textStyles.body2,
                )
                Text(
                    text = home.status.hint,
                    color = content,
                    style = MiuixTheme.textStyles.footnote1,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .alpha(0.75f),
                )
            }
        }
    }
}

/** A one-line row: icon, what it is, and its hint. */
@Composable
private fun CompactStatusCard(row: InfoRow) {
    val (container, content) = colorsFor(if (row.active) Tone.OK else Tone.WARN)

    Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        colors = CardDefaults.defaultColors(color = container, contentColor = content),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = row.icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(20.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Text(text = row.title, color = content, style = MiuixTheme.textStyles.body2)
                Text(
                    text = row.summary,
                    color = content,
                    style = MiuixTheme.textStyles.footnote1,
                    modifier = Modifier.alpha(0.75f),
                )
            }
            Text(
                text = row.hint,
                color = content,
                style = MiuixTheme.textStyles.footnote1,
                modifier = Modifier.alpha(0.75f),
            )
        }
    }
}

/** One cell of the statistics row: a label and a value. */
@Composable
private fun StatisticCard(stat: Stat, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = stat.title,
            style = MiuixTheme.textStyles.footnote2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stat.value,
            style = MiuixTheme.textStyles.title3,
            color = MiuixTheme.colorScheme.onSurface,
        )
    }
}

/** The tone palette every block on this page shares. */
@Composable
private fun colorsFor(tone: Tone): Pair<Color, Color> = when (tone) {
    Tone.OK -> MiuixTheme.colorScheme.primaryContainer to MiuixTheme.colorScheme.onPrimaryContainer
    Tone.WARN, Tone.BAD ->
        MiuixTheme.colorScheme.errorContainer to MiuixTheme.colorScheme.onErrorContainer
    Tone.BUSY, Tone.IDLE ->
        MiuixTheme.colorScheme.surfaceContainerHigh to MiuixTheme.colorScheme.onSurfaceContainerHigh
}
