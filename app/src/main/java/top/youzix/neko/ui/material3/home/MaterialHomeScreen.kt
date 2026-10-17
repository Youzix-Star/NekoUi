/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * The page skeleton and the overview rows follow InstallerX-Revived's Material 3 home page
 * (GPL-3.0-only), which may be combined with this project's AGPL-3.0-only code under GPLv3 §13.
 * The block order and the padding values match DSHA's home page so the two engines stay in step.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package top.youzix.neko.ui.material3.home

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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import top.youzix.neko.demo.Home
import top.youzix.neko.demo.InfoRow
import top.youzix.neko.demo.Stat
import top.youzix.neko.demo.Tone
import top.youzix.neko.demo.demoHome
import top.youzix.neko.ui.material3.CornerRadius
import top.youzix.neko.ui.material3.components.StatusTag
import top.youzix.neko.ui.material3.material3AppBarColor
import top.youzix.neko.ui.material3.material3BlurEffect
import top.youzix.neko.ui.material3.rememberMaterial3BlurBackdrop
import top.youzix.neko.ui.material3.components.IconTile
import top.youzix.neko.ui.material3.components.SegmentedColumn
import top.youzix.neko.ui.material3.components.SegmentedListItem
import top.yukonga.miuix.kmp.blur.layerBackdrop

/**
 * 首页 — the Material 3 twin of the miuix page: same demo content, same block order, same padding.
 * Changing one engine's home page and not the other is the one thing the 双引擎 rule forbids.
 */
@Composable
fun MaterialHomeScreen(
    outerPadding: PaddingValues,
    useBlur: Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val backdrop = rememberMaterial3BlurBackdrop(useBlur)
    val home = demoHome()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeFlexibleTopAppBar(
                modifier = Modifier.material3BlurEffect(backdrop),
                title = {
                    Text(
                        text = "NekoUi",
                        modifier = Modifier.padding(start = 12.dp),
                    )
                },
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
            item { StatusCard(home) }

            items(home.rows.size) { index ->
                CompactStatusCard(home.rows[index])
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    home.stats.forEach { stat ->
                        StatisticCard(stat, modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                SegmentedColumn(
                    title = "概览",
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    home.overview.forEach { entry ->
                        item(key = entry.title) {
                            SegmentedListItem(
                                headlineContent = { Text(entry.title) },
                                supportingContent = { Text(entry.summary) },
                                leadingContent = { IconTile(entry.icon, entry.title) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * The main block: a panel coloured by state and a large faint icon as a watermark.
 */
@Composable
private fun StatusCard(home: Home) {
    val (container, content) = colorsFor(home.status.tone)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = container,
        shape = RoundedCornerShape(CornerRadius),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Icon(
                imageVector = home.icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(88.dp)
                    .alpha(0.16f),
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = home.status.headline,
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = content,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = home.status.detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = content.copy(alpha = 0.9f),
                )
                Text(
                    text = home.status.hint,
                    style = MaterialTheme.typography.labelSmall,
                    color = content.copy(alpha = 0.75f),
                    modifier = Modifier.padding(top = 8.dp),
                )
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    home.tags.forEach { tag ->
                        StatusTag(
                            label = tag.label,
                            backgroundColor = content.copy(alpha = 0.12f),
                            contentColor = content,
                        )
                    }
                }
            }
        }
    }
}

/** A one-line row. */
@Composable
private fun CompactStatusCard(row: InfoRow) {
    val (container, content) = colorsFor(if (row.active) Tone.OK else Tone.WARN)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = container,
        shape = RoundedCornerShape(CornerRadius),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
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
                Text(
                    text = row.title,
                    style = MaterialTheme.typography.bodyMediumEmphasized,
                    color = content,
                )
                Text(
                    text = row.summary,
                    style = MaterialTheme.typography.labelSmall,
                    color = content.copy(alpha = 0.75f),
                )
            }
            Text(
                text = row.hint,
                style = MaterialTheme.typography.labelSmall,
                color = content.copy(alpha = 0.75f),
            )
        }
    }
}

/** One cell of the statistics row. */
@Composable
private fun StatisticCard(stat: Stat, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                text = stat.title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleMediumEmphasized,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/** The tone palette every block on this page shares. */
@Composable
private fun colorsFor(tone: Tone): Pair<Color, Color> = when (tone) {
    Tone.OK -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    Tone.WARN, Tone.BAD ->
        MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    Tone.BUSY, Tone.IDLE, Tone.ACCENT ->
        MaterialTheme.colorScheme.surfaceContainerHigh to MaterialTheme.colorScheme.onSurface
}
