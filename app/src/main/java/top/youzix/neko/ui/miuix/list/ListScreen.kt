/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * The page follows SukiSU-Ultra's SuperUserMiuix.kt (GPL-3.0): a Miuix search bar, a
 * pull-to-refresh body and one card per group.
 */

package top.youzix.neko.ui.miuix.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.youzix.neko.demo.ListDemo
import top.youzix.neko.demo.ListGroup
import top.youzix.neko.demo.ListItem
import top.youzix.neko.demo.TagTone
import top.youzix.neko.ui.miuix.components.StatusTag
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SearchBar
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

/**
 * 列表 — a searchable, grouped list with pull-to-refresh.
 */
@Composable
fun ListScreen(
    contentPadding: PaddingValues,
    scrollBehavior: ScrollBehavior,
) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<ListItem?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val groups = ListDemo.search(query)

    PullToRefresh(
        isRefreshing = isRefreshing,
        onRefresh = {
            if (!isRefreshing) {
                isRefreshing = true
                scope.launch {
                    delay(700)
                    isRefreshing = false
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentPadding = contentPadding,
        topAppBarScrollBehavior = scrollBehavior,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "search") {
                SearchBar(
                    inputField = {
                        TextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = "搜索",
                            singleLine = true,
                        )
                    },
                    onExpandedChange = {},
                    expanded = false,
                    content = {},
                )
            }

            if (groups.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = "没有匹配「$query」的条目",
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                }
            } else {
                groups.forEach { group ->
                    item(key = group.title) {
                        GroupCard(group = group, onSelect = { selected = it })
                    }
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
                Button(
                    onClick = { selected = null },
                    modifier = Modifier.fillMaxWidth(),
                ) {
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

@Composable
private fun tagColors(tone: TagTone) = when (tone) {
    TagTone.OK -> MiuixTheme.colorScheme.primaryContainer to MiuixTheme.colorScheme.onPrimaryContainer
    TagTone.WARN -> MiuixTheme.colorScheme.errorContainer to MiuixTheme.colorScheme.onErrorContainer
    TagTone.ACCENT -> MiuixTheme.colorScheme.secondaryContainer to
        MiuixTheme.colorScheme.onSecondaryContainer
    TagTone.NEUTRAL -> MiuixTheme.colorScheme.surfaceContainerHigh to
        MiuixTheme.colorScheme.onSurfaceContainerHigh
}
