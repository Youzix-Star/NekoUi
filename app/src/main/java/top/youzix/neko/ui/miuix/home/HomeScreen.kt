/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * The page is SukiSU-Ultra's miuix home page (GPL-3.0): a hero card with a watermark glyph, the
 * numbers under it, a card of link rows and two spec sheets. The hierarchy is the point — one big
 * block, then numbers, then rows — instead of a stack of equally-sized cards.
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.youzix.neko.demo.Hero
import top.youzix.neko.demo.HomeLink
import top.youzix.neko.demo.HomeTarget
import top.youzix.neko.demo.SpecRow
import top.youzix.neko.demo.Stat
import top.youzix.neko.demo.Tag
import top.youzix.neko.demo.Tone
import top.youzix.neko.demo.demoHome
import top.youzix.neko.ui.miuix.components.StatusTag
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.unit.Dp
import top.youzix.neko.ui.miuix.components.BlurredBar
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar

/**
 * 首页 — the shell's front page.
 */
@Composable
fun HomeScreen(
    bottomPadding: Dp,
    onOpenLink: (HomeTarget) -> Unit,
) {
    val scrollBehavior = MiuixScrollBehavior()
    Scaffold(
        topBar = {
            BlurredBar(backdrop = null) {
                TopAppBar(title = "NekoUi", scrollBehavior = scrollBehavior)
            }
        },
        contentWindowInsets = WindowInsets.systemBars
            .add(WindowInsets.displayCutout)
            .only(WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        HomeContent(
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + bottomPadding,
            ),
            scrollBehavior = scrollBehavior,
            onOpenLink = onOpenLink,
        )
    }
}

@Composable
private fun HomeContent(
    contentPadding: PaddingValues,
    scrollBehavior: ScrollBehavior,
    onOpenLink: (HomeTarget) -> Unit,
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
        item(key = "hero") {
            HeroCard(home.hero)
        }
        item(key = "stats") {
            StatsRow(home.stats)
        }
        item(key = "links") {
            Links(home.links, onOpenLink)
        }
        item(key = "system") {
            SpecSheet(home.system)
        }
        item(key = "build") {
            SpecSheet(home.build)
        }
    }
}

/**
 * The hero: a tinted card whose watermark glyph sits bottom-right, tags along the top, and the
 * title at 22sp — the first thing a thumb lands on.
 */
@Composable
private fun HeroCard(hero: Hero) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.defaultColors(color = colorScheme.secondaryContainer),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(24.dp, 28.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Icon(
                    imageVector = hero.icon,
                    contentDescription = null,
                    modifier = Modifier.size(110.dp),
                    tint = colorScheme.primary.copy(alpha = 0.8f),
                )
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    hero.tags.forEach { tag ->
                        val (background, content) = tagColors(tag)
                        StatusTag(
                            label = tag.label,
                            backgroundColor = background,
                            contentColor = content,
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = hero.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSecondaryContainer,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = hero.subtitle,
                    fontSize = 15.sp,
                    color = colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                )
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
            Card(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .height(88.dp)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stat.count,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.onSurface,
                    )
                    Text(
                        text = stat.label,
                        style = MiuixTheme.textStyles.footnote1,
                        color = colorScheme.onSurfaceVariantSummary,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/** The two ways out of the home page. */
@Composable
private fun Links(links: List<HomeLink>, onOpenLink: (HomeTarget) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        links.forEach { link ->
            ArrowPreference(
                title = link.title,
                summary = link.summary,
                startAction = {
                    Icon(
                        imageVector = link.icon,
                        contentDescription = link.title,
                        modifier = Modifier.padding(end = 6.dp),
                        tint = colorScheme.onBackground,
                    )
                },
                onClick = { onOpenLink(link.target) },
            )
        }
    }
}

/**
 * A sheet of label/value rows, values in monospace and right-aligned — the shape a "system
 * information" block wants: scannable on the left, precise on the right.
 */
@Composable
private fun SpecSheet(rows: List<SpecRow>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            rows.forEachIndexed { index, row ->
                if (index > 0) {
                    HorizontalDivider(color = colorScheme.dividerLine)
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
                        style = MiuixTheme.textStyles.footnote1,
                        color = colorScheme.onSurfaceVariantSummary,
                    )
                    Text(
                        text = row.value,
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        color = colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

/** Tag colours on the hero. */
@Composable
private fun tagColors(tag: Tag): Pair<Color, Color> = when (tag.tone) {
    Tone.ACCENT -> colorScheme.primary to colorScheme.onPrimary
    Tone.OK -> colorScheme.tertiaryContainer to colorScheme.onTertiaryContainer
    Tone.WARN, Tone.BAD -> colorScheme.errorContainer to colorScheme.onErrorContainer
    Tone.BUSY, Tone.IDLE ->
        colorScheme.surfaceContainerHigh to colorScheme.onSurfaceContainerHigh
}
