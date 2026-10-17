/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.ui.miuix.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.youzix.neko.demo.ListDemo
import top.youzix.neko.demo.ListGroup
import top.youzix.neko.demo.ListItem
import top.youzix.neko.demo.TagTone
import top.youzix.neko.ui.AppIconText
import top.youzix.neko.ui.miuix.components.StatusTag
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

/**
 * 列表 — a searchable, grouped list.
 *
 * The shape is the point: a search field that filters locally, one card per group, a status tag on
 * the trailing edge of each row, an empty state that says what to do next, and a row tap that opens
 * a dialog. Copy this page's skeleton for any list a new app needs.
 */
@Composable
fun ListScreen(
    contentPadding: PaddingValues,
    scrollBehavior: ScrollBehavior,
) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<ListItem?>(null) }
    val groups = ListDemo.search(query)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .overScrollVertical(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "search") {
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = "搜索",
                singleLine = true,
            )
        }

        if (groups.isEmpty()) {
            item(key = "empty") {
                EmptyState(query = query)
            }
        } else {
            groups.forEach { group ->
                item(key = group.title) {
                    GroupCard(group = group, onSelect = { selected = it })
                }
            }
        }
    }

    selected?.let { item ->
        OverlayDialog(
            show = true,
            title = item.title,
            onDismissRequest = { selected = null },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = item.detail.ifEmpty { item.summary },
                    style = MiuixTheme.textStyles.body2,
                )
                Button(onClick = { selected = null }, modifier = Modifier.fillMaxWidth()) {
                    Text("关闭", style = MiuixTheme.textStyles.button)
                }
            }
        }
    }
}

@Composable
private fun GroupCard(group: ListGroup, onSelect: (ListItem) -> Unit) {
    Column {
        SmallTitle(text = group.title)
        Card(modifier = Modifier.fillMaxWidth()) {
            group.items.forEach { item ->
                BasicComponent(
                    title = item.title,
                    summary = item.summary,
                    onClick = { onSelect(item) },
                    endActions = {
                        item.tag?.let { tag ->
                            val (background, content) = tagColors(item.tone)
                            StatusTag(label = tag, backgroundColor = background, contentColor = content)
                        }
                    },
                )
            }
        }
    }
}

/** The empty state: say what was searched for, and what to do about it. */
@Composable
private fun EmptyState(query: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(horizontal = 20.dp, vertical = 32.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = AppIconText,
                fontSize = 32.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "没有匹配「$query」的条目",
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "共 ${ListDemo.total} 条，换个关键词试试。",
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

/** Tag colours, shared by every row on this page. */
@Composable
private fun tagColors(tone: TagTone) = when (tone) {
    TagTone.OK -> MiuixTheme.colorScheme.primaryContainer to MiuixTheme.colorScheme.onPrimaryContainer
    TagTone.WARN -> MiuixTheme.colorScheme.errorContainer to MiuixTheme.colorScheme.onErrorContainer
    TagTone.ACCENT -> MiuixTheme.colorScheme.secondaryContainer to
        MiuixTheme.colorScheme.onSecondaryContainer
    TagTone.NEUTRAL -> MiuixTheme.colorScheme.surfaceContainerHigh to
        MiuixTheme.colorScheme.onSurfaceContainerHigh
}
