/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package top.youzix.neko.ui.material3.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import top.youzix.neko.ui.material3.material3AppBarColor
import top.youzix.neko.ui.material3.material3BlurEffect
import top.youzix.neko.ui.material3.rememberMaterial3BlurBackdrop
import top.youzix.neko.ui.material3.components.StatusTag
import top.youzix.neko.ui.material3.components.TonalCard
import top.youzix.neko.ui.material3.widgets.BaseWidget
import top.youzix.neko.ui.material3.widgets.SegmentedColumn
import top.yukonga.miuix.kmp.blur.layerBackdrop

/**
 * 列表 — a searchable, grouped list.
 *
 * The shape is the point: a search field that filters locally, one segmented group per section, a
 * status tag on the trailing edge of each row, an empty state that says what to do next, and a row
 * tap that opens a dialog.
 */
@Composable
fun MaterialListScreen(
    outerPadding: PaddingValues,
    useBlur: Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val backdrop = rememberMaterial3BlurBackdrop(useBlur)
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<ListItem?>(null) }
    val groups = ListDemo.search(query)

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeFlexibleTopAppBar(
                modifier = Modifier.material3BlurEffect(backdrop),
                title = { Text("列表", modifier = Modifier.padding(start = 12.dp)) },
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
            item(key = "search") {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("搜索") },
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
                        GroupColumn(group = group, onSelect = { selected = it })
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
            confirmButton = {
                TextButton(onClick = { selected = null }) { Text("关闭") }
            },
        )
    }
}

@Composable
private fun GroupColumn(group: ListGroup, onSelect: (ListItem) -> Unit) {
    SegmentedColumn(title = group.title) {
        group.items.forEach { item ->
            item(key = item.id) {
                BaseWidget(
                    title = item.title,
                    description = item.summary,
                    iconPlaceholder = false,
                    onClick = { onSelect(item) },
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

/** The empty state: say what was searched for, and what to do about it. */
@Composable
private fun EmptyState(query: String) {
    TonalCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = AppIconText,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "没有匹配「$query」的条目",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "共 ${ListDemo.total} 条，换个关键词试试。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Tag colours, shared by every row on this page. */
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
