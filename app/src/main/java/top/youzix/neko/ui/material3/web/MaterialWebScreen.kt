/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package top.youzix.neko.ui.material3.web

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
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import top.youzix.neko.ui.material3.material3AppBarColor
import top.youzix.neko.ui.material3.material3BlurEffect
import top.youzix.neko.ui.material3.rememberMaterial3BlurBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop

/**
 * 网页 — the browser shell, in the Material 3 engine.
 *
 * The address row is real UI; the page under it is not part of this build. The state lives in
 * [WebShell], outside the page, because this tab is disposed every time the pager scrolls away.
 */
@Composable
fun MaterialWebScreen(
    outerPadding: PaddingValues,
    useBlur: Boolean,
) {
    val backdrop = rememberMaterial3BlurBackdrop(useBlur)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeFlexibleTopAppBar(
                modifier = Modifier.material3BlurEffect(backdrop),
                title = { Text("网页", modifier = Modifier.padding(start = 12.dp)) },
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
                .padding(paddingValues + outerPadding),
        ) {
            // The strip is a list of one. That is what makes dragging it float the title and
            // stretch at the boundaries exactly as it does on 关于.
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item(key = "address") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { WebShell.goBack() }) {
                            Icon(
                                imageVector = AppIcons.Back,
                                contentDescription = "后退",
                                modifier = Modifier.alpha(if (WebShell.canGoBack) 1f else 0.38f),
                            )
                        }
                        IconButton(onClick = { WebShell.goForward() }) {
                            Icon(
                                imageVector = AppIcons.Forward,
                                contentDescription = "前进",
                                modifier = Modifier.alpha(if (WebShell.canGoForward) 1f else 0.38f),
                            )
                        }
                        OutlinedTextField(
                            value = WebShell.address,
                            onValueChange = { WebShell.onAddressChange(it) },
                            modifier = Modifier.weight(1f),
                            label = { Text("地址") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(onGo = { WebShell.submit() }),
                        )
                        IconButton(onClick = { WebShell.reload() }) {
                            Icon(imageVector = AppIcons.Refresh, contentDescription = "刷新")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(BrowserCardInset),
            ) {
                if (WebShell.page.isEmpty()) StartState() else Placeholder(WebShell.page)
            }
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
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "输入地址开始浏览",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            style = MaterialTheme.typography.labelMedium,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "这个构建没有内置网页视图。",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
