/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.ui.miuix

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import top.youzix.neko.ui.theme.ColorMode
import top.youzix.neko.ui.theme.miuixColorSpecOf
import top.youzix.neko.ui.theme.miuixPaletteStyleOf
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController

/**
 * Dark flag of the colour scheme Miuix currently resolves to.
 *
 * The vendored liquid-glass components need it, and reading it from the composition keeps every
 * layer in agreement with [ThemeController].
 */
private val LocalIsDarkTheme = staticCompositionLocalOf { false }

@Composable
fun isInDarkTheme(): Boolean = LocalIsDarkTheme.current

/**
 * The miuix engine's theme: the same colour mode, seed colour, palette style and spec the Material
 * engine reads, handed to miuix's own [ThemeController].
 */
@Composable
fun MiuixAppTheme(
    colorMode: ColorMode,
    keyColor: Int,
    paletteStyle: String,
    colorSpec: String,
    content: @Composable () -> Unit,
) {
    val isDark = colorMode.isDark()
    val seed = if (keyColor == 0) null else Color(keyColor)
    val controller = remember(colorMode, keyColor, paletteStyle, colorSpec) {
        ThemeController(
            colorSchemeMode = colorMode.toMiuix(),
            keyColor = seed,
            paletteStyle = miuixPaletteStyleOf(paletteStyle),
            colorSpec = miuixColorSpecOf(colorSpec),
        )
    }
    MiuixTheme(controller = controller) {
        CompositionLocalProvider(LocalIsDarkTheme provides isDark, content = content)
    }
}
