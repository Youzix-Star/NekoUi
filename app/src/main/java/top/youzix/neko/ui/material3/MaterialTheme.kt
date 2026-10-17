/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.ui.material3

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val isDark = colorMode.isDark()
    val canUseWallpaper = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val scheme = when {
        !colorMode.isMonet -> defaultMaterialScheme(isDark)
        canUseWallpaper ->
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        else -> defaultMaterialScheme(isDark)
    }

    MaterialTheme(colorScheme = scheme, content = content)
}
