/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 *
 * The theme vocabulary is SukiSU-Ultra's (GPL-3.0): one colour mode, one seed colour, one palette
 * style and one colour spec, shared by both engines.
 */

package top.youzix.neko.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.ThemeColorSpec
import top.yukonga.miuix.kmp.theme.ThemePaletteStyle

/** What the theme page's first row offers. */
enum class ColorMode(val label: String) {
    System("跟随系统"),
    Light("浅色"),
    Dark("深色"),
    MonetSystem("动态取色 · 跟随系统"),
    MonetLight("动态取色 · 浅色"),
    MonetDark("动态取色 · 深色"),
    ;

    val isMonet: Boolean get() = this == MonetSystem || this == MonetLight || this == MonetDark

    @Composable
    fun isDark(): Boolean = when (this) {
        Light, MonetLight -> false
        Dark, MonetDark -> true
        System, MonetSystem -> isSystemInDarkTheme()
    }

    fun toMiuix(): ColorSchemeMode = when (this) {
        System -> ColorSchemeMode.System
        Light -> ColorSchemeMode.Light
        Dark -> ColorSchemeMode.Dark
        MonetSystem -> ColorSchemeMode.MonetSystem
        MonetLight -> ColorSchemeMode.MonetLight
        MonetDark -> ColorSchemeMode.MonetDark
    }

    companion object {
        fun fromName(name: String?): ColorMode =
            entries.firstOrNull { it.name == name } ?: System
    }
}

/** Seed colours the theme page offers; `0` in the stored value means "follow the wallpaper". */
val SeedPresets: List<Int> = listOf(
    0xFF6750A4.toInt(),
    0xFF00A67E.toInt(),
    0xFFE0602C.toInt(),
    0xFF3B82F6.toInt(),
    0xFFE11D48.toInt(),
    0xFF9C6BFF.toInt(),
    0xFFC8A96A.toInt(),
    0xFF2E7D32.toInt(),
)

/** The palette styles both MaterialKolor and miuix know by name. */
val PaletteStyles: List<String> = listOf(
    "TonalSpot", "Neutral", "Vibrant", "Expressive", "Rainbow",
    "FruitSalad", "Monochrome", "Fidelity", "Content",
)

/** The colour specs both libraries know. */
val ColorSpecs: List<String> = listOf("SPEC_2021", "SPEC_2025")

fun paletteStyleOf(name: String): PaletteStyle =
    runCatching { PaletteStyle.valueOf(name) }.getOrDefault(PaletteStyle.TonalSpot)

fun colorSpecOf(name: String): ColorSpec.SpecVersion =
    runCatching { ColorSpec.SpecVersion.valueOf(name) }.getOrDefault(ColorSpec.SpecVersion.SPEC_2021)

fun miuixPaletteStyleOf(name: String): ThemePaletteStyle =
    runCatching { ThemePaletteStyle.valueOf(name) }.getOrDefault(ThemePaletteStyle.TonalSpot)

fun miuixColorSpecOf(name: String): ThemeColorSpec =
    runCatching { ThemeColorSpec.valueOf(name.replace("SPEC_", "Spec")) }
        .getOrDefault(ThemeColorSpec.Spec2021)

/** SukiSU only supports the 2025 spec on these styles; fall back to 2021 on the others. */
fun ColorSpec.SpecVersion.effectiveFor(style: PaletteStyle): ColorSpec.SpecVersion =
    if (this == ColorSpec.SpecVersion.SPEC_2025 &&
        style !in setOf(PaletteStyle.TonalSpot, PaletteStyle.Neutral, PaletteStyle.Vibrant, PaletteStyle.Expressive)
    ) {
        ColorSpec.SpecVersion.SPEC_2021
    } else {
        this
    }

/** The default (non-monet) Material scheme. */
fun defaultMaterialScheme(dark: Boolean) = if (dark) darkColorScheme() else lightColorScheme()
