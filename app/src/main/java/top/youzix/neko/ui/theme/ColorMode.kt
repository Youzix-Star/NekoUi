/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.theme.ColorSchemeMode

/** The theme choice offered in Settings. */
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
        fun fromName(name: String?): ColorMode = entries.firstOrNull { it.name == name } ?: System
    }
}

/** The Material engine's own scheme, used outside the dynamic-colour modes. */
fun defaultMaterialScheme(dark: Boolean) = if (dark) darkColorScheme() else lightColorScheme()
