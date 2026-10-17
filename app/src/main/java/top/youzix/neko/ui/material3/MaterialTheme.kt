/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.ui.material3

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import top.youzix.neko.ui.theme.ColorMode
import top.youzix.neko.ui.theme.defaultMaterialScheme

/**
 * The Material engine's theme.
 *
 * A plain mode uses Material's own scheme; a dynamic-colour mode takes the wallpaper palette where
 * the platform has one and falls back to the plain scheme where it does not.
 */
@Composable
fun MaterialAppTheme(
    colorMode: ColorMode,
    content: @Composable () -> Unit,
) {
    val scheme = defaultMaterialScheme(colorMode.isDark())
    MaterialTheme(colorScheme = scheme, content = content)
}
