/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * The page follows SukiSU-Ultra's SuperUserMiuix.kt (GPL-3.0): the granted/total summary, the
 * search bar, the pull-to-refresh body and one card of rows.
 */

package top.youzix.neko.ui.miuix.list

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import top.youzix.neko.demo.AppEntry
import top.youzix.neko.demo.ListDemo
import top.youzix.neko.demo.ListFilter
import top.youzix.neko.demo.TagTone
import top.youzix.neko.ui.miuix.components.StatusTag
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SearchBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 列表 — a searchable superuser-style list with pull-to-refresh.
 */
@Composable
fun ListScreen(
    contentPadding: PaddingValues,
    scrollBehavior: ScrollBehavior,
) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(ListFilter.All) }
    var selected by remember { mutableStateOf<AppEntry?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val entries = ListDemo.filter(query, filter)

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
        topAppBarScrollBehavior = scrollBehavior,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
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

            item(key = "header") {
                ListHeader(
                    granted = ListDemo.granted,
                    total = ListDemo.total,
                    filter = filter,
                    onFilterChange = { filter = it },
                )
            }

            item(key = "apps") {
                Card(modifier = Modifier.fillMaxWidth()) {
                    entries.forEach { app ->
                        BasicComponent(
                            title = app.name,
                            summary = app.packageName,
                            onClick = { selected = app },
                            endActions = {
                                val (background, content) = tagColors(app.tone)
                                StatusTag(
                                    label = app.note,
                                    backgroundColor = background,
                                    contentColor = content,
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    selected?.let { app ->
        OverlayDialog(
            show = true,
            title = app.name,
            summary = "${app.packageName} · ${app.note}",
            onDismissRequest = { selected = null },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { selected = null }, modifier = Modifier.fillMaxWidth()) {
                    Text("关闭", style = MiuixTheme.textStyles.button)
                }
            }
        }
    }
}

/** The granted/total summary and the filters. */
@Composable
private fun ListHeader(
    granted: Int,
    total: Int,
    filter: ListFilter,
    onFilterChange: (ListFilter) -> Unit,
) {
    Column(
        modifier = Modifier.padding(bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = "已授权 $granted / $total 个应用",
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterButton("全部", filter == ListFilter.All) { onFilterChange(ListFilter.All) }
            FilterButton("已授权", filter == ListFilter.Granted) { onFilterChange(ListFilter.Granted) }
            FilterButton("未授权", filter == ListFilter.Denied) { onFilterChange(ListFilter.Denied) }
        }
    }
}

@Composable
private fun FilterButton(label: String, selected: Boolean, onClick: () -> Unit) {
    TextButton(
        text = label,
        onClick = onClick,
        colors = if (selected) {
            top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColorsPrimary()
        } else {
            top.yukonga.miuix.kmp.basic.ButtonDefaults.textButtonColors()
        },
    )
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
