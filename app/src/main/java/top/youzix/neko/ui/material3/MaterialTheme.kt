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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.materialkolor.rememberDynamicColorScheme
import top.youzix.neko.ui.theme.ColorMode
import top.youzix.neko.ui.theme.colorSpecOf
import top.youzix.neko.ui.theme.defaultMaterialScheme
import top.youzix.neko.ui.theme.effectiveFor
import top.youzix.neko.ui.theme.paletteStyleOf

/** Used when the platform cannot hand us the wallpaper colour. */
private val FallbackSeed = Color(0xFF6750A4)

/**
 * The Material engine's theme, generated the way SukiSU does it: a non-monet mode is Material's
 * own scheme, a monet mode is either the wallpaper's palette (seed `0`) or a palette generated
 * from the seed colour with the chosen style and spec.
 */
@Composable
fun MaterialAppTheme(
    colorMode: ColorMode,
    keyColor: Int,
    paletteStyle: String,
    colorSpec: String,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val isDark = colorMode.isDark()
    val style = paletteStyleOf(paletteStyle)
    val spec = colorSpecOf(colorSpec)

    val scheme = when {
        !colorMode.isMonet -> defaultMaterialScheme(isDark)

        keyColor != 0 -> rememberDynamicColorScheme(
            seedColor = Color(keyColor),
            isDark = isDark,
            isAmoled = false,
            style = style,
            specVersion = spec.effectiveFor(style),
        )

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

        else -> rememberDynamicColorScheme(
            seedColor = FallbackSeed,
            isDark = isDark,
            isAmoled = false,
            style = style,
            specVersion = spec.effectiveFor(style),
        )
    }

    MaterialTheme(colorScheme = scheme, content = content)
}
