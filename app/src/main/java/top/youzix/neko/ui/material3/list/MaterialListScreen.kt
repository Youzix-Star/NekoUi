/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * The page follows SukiSU-Ultra's SuperUserMaterial.kt (GPL-3.0): a search app bar that expands to
 * a full-screen search, a pull-to-refresh body, and segmented rows. The search field is SukiSU's
 * `SearchAppBar`, not a plain text field.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package top.youzix.neko.ui.material3.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.youzix.neko.demo.ListDemo
import top.youzix.neko.demo.ListGroup
import top.youzix.neko.demo.ListItem
import top.youzix.neko.demo.TagTone
import top.youzix.neko.ui.material3.components.ExpressiveScaffold
import top.youzix.neko.ui.material3.components.SearchAppBar
import top.youzix.neko.ui.material3.components.SegmentedColumn
import top.youzix.neko.ui.material3.components.SegmentedListItem
import top.youzix.neko.ui.material3.components.StatusTag

/**
 * 列表 — a searchable, grouped list with pull-to-refresh.
 */
@Composable
fun MaterialListScreen(
    outerPadding: androidx.compose.foundation.layout.PaddingValues,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val pullState = rememberPullToRefreshState()

    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<ListItem?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    val groups = ListDemo.search(query)

    ExpressiveScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SearchAppBar(
                title = { Text("列表", modifier = Modifier.padding(start = 12.dp)) },
                searchText = query,
                onSearchTextChange = { query = it },
                onClearClick = { query = "" },
                snackbarHostState = snackbarHostState,
                scrollBehavior = scrollBehavior,
                defaultContent = { bottomPadding, _ ->
                    SearchResults(groups = groups, bottomPadding = bottomPadding)
                },
                searchContent = { bottomPadding, _ ->
                    SearchResults(groups = groups, bottomPadding = bottomPadding)
                },
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
        ),
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(outerPadding),
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
            state = pullState,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = isRefreshing,
                    state = pullState,
                )
            },
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                ),
            ) {
                if (groups.isEmpty()) {
                    item(key = "empty") {
                        EmptyState(query = query)
                    }
                } else {
                    groups.forEach { group ->
                        item(key = group.title) {
                            GroupColumn(group = group, onSelect = { selected = it })
                        }
                    }
                }
            }
        }
    }

    selected?.let { item ->
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(item.title) },
            text = { Text(item.detail.ifEmpty { item.summary }) },
            confirmButton = { TextButton(onClick = { selected = null }) { Text("关闭") } },
        )
    }
}

/** The rows rendered inside the expanded search, where pull-to-refresh has no place. */
@Composable
private fun SearchResults(groups: List<ListGroup>, bottomPadding: Dp) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp + bottomPadding,
        ),
    ) {
        groups.forEach { group ->
            item(key = group.title) {
                GroupColumn(group = group, onSelect = {})
            }
        }
    }
}

@Composable
private fun GroupColumn(group: ListGroup, onSelect: (ListItem) -> Unit) {
    SegmentedColumn(title = group.title) {
        group.items.forEach { item ->
            item(key = item.id) {
                SegmentedListItem(
                    onClick = { onSelect(item) },
                    headlineContent = { Text(item.title) },
                    supportingContent = { Text(item.summary) },
                    trailingContent = {
                        item.tag?.let { tag ->
                            val (background, content) = tagColors(item.tone)
                            StatusTag(
                                label = tag,
                                backgroundColor = background,
                                contentColor = content,
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun EmptyState(query: String) {
    Text(
        text = "没有匹配「$query」的条目",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 24.dp),
    )
}

@Composable
private fun tagColors(tone: TagTone) = when (tone) {
    TagTone.OK -> MaterialTheme.colorScheme.primaryContainer to
        MaterialTheme.colorScheme.onPrimaryContainer
    TagTone.WARN -> MaterialTheme.colorScheme.errorContainer to
        MaterialTheme.colorScheme.onErrorContainer
    TagTone.ACCENT -> MaterialTheme.colorScheme.secondaryContainer to
        MaterialTheme.colorScheme.onSecondaryContainer
    TagTone.NEUTRAL -> MaterialTheme.colorScheme.surfaceContainerHigh to
        MaterialTheme.colorScheme.onSurfaceVariant
}
