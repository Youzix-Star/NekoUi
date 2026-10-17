/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * The page is SukiSU-Ultra's home page (GPL-3.0): a hero card with the state rings, three oversized
 * statistics, a segmented list of links, then two spec sheets. The point is the hierarchy — one
 * huge block, then numbers, then rows — rather than a stack of equally-sized cards.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)

package top.youzix.neko.ui.material3.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import top.youzix.neko.demo.Hero
import top.youzix.neko.demo.HomeLink
import top.youzix.neko.demo.HomeTarget
import top.youzix.neko.demo.SpecRow
import top.youzix.neko.demo.Stat
import top.youzix.neko.demo.Tag
import top.youzix.neko.demo.Tone
import top.youzix.neko.demo.demoHome
import top.youzix.neko.ui.AppIcons
import top.youzix.neko.ui.material3.components.IconTile
import top.youzix.neko.ui.material3.components.SegmentedColumn
import top.youzix.neko.ui.material3.components.SegmentedListItem
import top.youzix.neko.ui.material3.components.StatusTag
import top.youzix.neko.ui.material3.material3AppBarColor
import top.youzix.neko.ui.material3.material3BlurEffect
import top.youzix.neko.ui.material3.rememberMaterial3BlurBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop

private val HeroShape = RoundedCornerShape(28.dp)
private val TileShape = RoundedCornerShape(22.dp)

/**
 * 首页 — the shell's front page.
 */
@Composable
fun MaterialHomeScreen(
    outerPadding: PaddingValues,
    useBlur: Boolean,
    onOpenLink: (HomeTarget) -> Unit,
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
                title = { Text("NekoUi", modifier = Modifier.padding(start = 12.dp)) },
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
                .verticalScroll(rememberScrollState())
                .padding(paddingValues + outerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HeroCard(home.hero)
            StatsRow(home.stats)
            Links(home.links, onOpenLink)
            SpecSheet(home.system)
            SpecSheet(home.build)
            Spacer(Modifier.height(8.dp))
        }
    }
}

/**
 * The hero: a filled surface with concentric state rings drawn behind the text, tags along the
 * top, and the title set in `displaySmall` — big enough that it is the first thing read.
 */
@Composable
private fun HeroCard(hero: Hero) {
    val container = MaterialTheme.colorScheme.primary
    val content = MaterialTheme.colorScheme.onPrimary

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = container,
        contentColor = content,
        shape = HeroShape,
    ) {
        Box(modifier = Modifier.drawBehind { drawStatusRings(content.copy(alpha = 0.18f)) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 188.dp)
                    .padding(22.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    hero.tags.forEach { tag ->
                        val (background, tagContent) = tagColors(tag)
                        StatusTag(
                            label = tag.label,
                            backgroundColor = background,
                            contentColor = tagContent,
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = hero.title,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        text = hero.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
        }
    }
}

/** Three oversized numbers. */
@Composable
private fun StatsRow(stats: List<Stat>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        stats.forEach { stat ->
            StatTile(stat, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatTile(stat: Stat, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = TileShape,
        color = MaterialTheme.colorScheme.surfaceBright,
    ) {
        Column(
            modifier = Modifier
                .height(88.dp)
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stat.count,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

/** The two ways out of the home page. */
@Composable
private fun Links(links: List<HomeLink>, onOpenLink: (HomeTarget) -> Unit) {
    SegmentedColumn(modifier = Modifier.fillMaxWidth()) {
        links.forEach { link ->
            item(key = link.title) {
                SegmentedListItem(
                    onClick = { onOpenLink(link.target) },
                    headlineContent = { Text(link.title) },
                    supportingContent = { Text(link.summary) },
                    leadingContent = { IconTile(link.icon, link.title) },
                    trailingContent = { Icon(AppIcons.ChevronRight, null) },
                )
            }
        }
    }
}

/**
 * A sheet of label/value rows, values in monospace and right-aligned — the shape a "system
 * information" block wants: scannable on the left, precise on the right.
 */
@Composable
private fun SpecSheet(rows: List<SpecRow>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = TileShape,
        color = MaterialTheme.colorScheme.surfaceBright,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            rows.forEachIndexed { index, row ->
                if (index > 0) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 46.dp)
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = row.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = row.value,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}

/** Tag colours on the hero: the accent tag inverts against the surface, the others stay tonal. */
@Composable
private fun tagColors(tag: Tag): Pair<Color, Color> = when (tag.tone) {
    Tone.ACCENT -> MaterialTheme.colorScheme.onPrimary to MaterialTheme.colorScheme.primary
    Tone.OK -> MaterialTheme.colorScheme.tertiaryContainer to
        MaterialTheme.colorScheme.onTertiaryContainer
    Tone.WARN, Tone.BAD -> MaterialTheme.colorScheme.errorContainer to
        MaterialTheme.colorScheme.onErrorContainer
    Tone.BUSY, Tone.IDLE -> MaterialTheme.colorScheme.secondaryContainer to
        MaterialTheme.colorScheme.onSecondaryContainer
}

/**
 * The rings in the hero's top-right corner: a solid outer circle, a dashed ring, a solid ring and a
 * filled dot. SukiSU's signature, and the reason the flat surface does not read as an empty box.
 */
private fun DrawScope.drawStatusRings(color: Color) {
    val center = Offset(size.width - 36.dp.toPx(), 32.dp.toPx())
    val stroke = 2.dp.toPx()
    drawCircle(color = color, radius = 112.dp.toPx(), center = center, style = Stroke(stroke))
    drawCircle(
        color = color,
        radius = 84.dp.toPx(),
        center = center,
        style = Stroke(
            stroke,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 8.dp.toPx())),
        ),
    )
    drawCircle(color = color, radius = 56.dp.toPx(), center = center, style = Stroke(stroke))
    drawCircle(color = color, radius = 18.dp.toPx(), center = center)
}
