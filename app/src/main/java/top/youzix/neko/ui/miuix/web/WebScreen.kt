/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.ui.miuix.web

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.youzix.neko.demo.WebShell
import top.youzix.neko.ui.AppIconText
import top.youzix.neko.ui.AppIcons
import top.youzix.neko.ui.miuix.shellTextFieldColors
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

/**
 * 网页 — the browser shell.
 *
 * The address row is real UI; the page under it is not part of this build. The state lives in
 * [WebShell] rather than here, because this tab is disposed every time the pager scrolls away.
 */
@Composable
fun WebScreen(
    contentPadding: PaddingValues,
    scrollBehavior: ScrollBehavior,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(contentPadding),
    ) {
        // Dragging this strip scrolls the page, so the top bar collapses and expands exactly as it
        // does on the home and about pages.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .overScrollVertical()
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { WebShell.goBack() }, enabled = WebShell.canGoBack) {
                    Icon(
                        imageVector = AppIcons.Back,
                        contentDescription = "后退",
                        modifier = Modifier.size(24.dp),
                    )
                }
                IconButton(onClick = { WebShell.goForward() }, enabled = WebShell.canGoForward) {
                    Icon(
                        imageVector = AppIcons.Forward,
                        contentDescription = "前进",
                        modifier = Modifier.size(24.dp),
                    )
                }
                TextField(
                    value = WebShell.address,
                    onValueChange = { WebShell.onAddressChange(it) },
                    modifier = Modifier.weight(1f),
                    colors = shellTextFieldColors(),
                    label = "地址",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(onGo = { WebShell.submit() }),
                )
                IconButton(onClick = { WebShell.reload() }) {
                    Icon(
                        imageVector = AppIcons.Refresh,
                        contentDescription = "刷新",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // The card. With no browser behind it, this is the whole of the page: the start state
        // until an address is submitted, then what was asked for.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(MiuixTheme.colorScheme.surfaceContainerHigh)
                .padding(BrowserCardInset),
        ) {
            if (WebShell.page.isEmpty()) StartState() else Placeholder(WebShell.page)
        }
    }
}

/** Inset that lets the card's rounded frame show around the page. */
private val BrowserCardInset = 10.dp

/** Shown while nothing has been submitted. */
@Composable
private fun StartState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = AppIconText,
            fontSize = 40.sp,
            color = MiuixTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "输入地址开始浏览",
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}

/** Shown once an address was submitted: the address, and why the page is not there. */
@Composable
private fun Placeholder(url: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = url,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = MiuixTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "这个构建没有内置网页视图。",
            style = MiuixTheme.textStyles.footnote2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}
