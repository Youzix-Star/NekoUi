/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.ui

import android.content.Context
import top.youzix.neko.ui.material3.ThemeMode
import top.youzix.neko.ui.predictiveback.PredictiveBackStyle
import top.yukonga.miuix.kmp.theme.ColorSchemeMode

/**
 * Which UI engine draws the app.
 *
 * The two engines are complete, independent implementations of the same four tabs — one built on
 * miuix, one on Material Design — and the user picks between them on the About page.
 */
enum class UiEngine(val id: String, val label: String) {
    Miuix("miuix", "Miuix"),
    Material3("material3", "Material Design"),
    ;

    companion object {
        fun from(id: String?): UiEngine = entries.firstOrNull { it.id == id } ?: Miuix
    }
}

/** Persists the selected engine, the glass-effect switch and the back-animation style. */
object UiEnginePrefs {
    private const val PREFS = "ui_prefs"
    private const val KEY_ENGINE = "ui_engine"
    private const val KEY_USE_BLUR = "use_blur"
    private const val KEY_PREDICTIVE_BACK = "predictive_back"
    private const val KEY_MIUIX_THEME = "miuix_theme"
    private const val KEY_MATERIAL_THEME = "material_theme"
    private const val KEY_DYNAMIC_COLOR = "dynamic_color"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(context: Context): UiEngine =
        UiEngine.from(prefs(context).getString(KEY_ENGINE, null))

    fun save(context: Context, engine: UiEngine) {
        prefs(context).edit().putString(KEY_ENGINE, engine.id).apply()
    }

    /**
     * Whether translucency is on: the miuix bottom bar's liquid glass and the Material 3 top
     * bar's blur. Defaults to `true`, matching the reference project.
     */
    fun loadUseBlur(context: Context): Boolean =
        prefs(context).getBoolean(KEY_USE_BLUR, true)

    fun saveUseBlur(context: Context, useBlur: Boolean) {
        prefs(context).edit().putBoolean(KEY_USE_BLUR, useBlur).apply()
    }

    /** Which predictive-back animation second-level pages use. */
    fun loadPredictiveBackStyle(context: Context): PredictiveBackStyle =
        PredictiveBackStyle.from(prefs(context).getString(KEY_PREDICTIVE_BACK, null))

    fun savePredictiveBackStyle(context: Context, style: PredictiveBackStyle) {
        prefs(context).edit().putString(KEY_PREDICTIVE_BACK, style.id).apply()
    }

    /** The miuix engine's theme mode, by enum name; defaults to dynamic colour following the system. */
    fun loadMiuixTheme(context: Context): ColorSchemeMode {
        val stored = prefs(context).getString(KEY_MIUIX_THEME, null) ?: return ColorSchemeMode.MonetSystem
        return runCatching { ColorSchemeMode.valueOf(stored) }.getOrDefault(ColorSchemeMode.MonetSystem)
    }

    fun saveMiuixTheme(context: Context, mode: ColorSchemeMode) {
        prefs(context).edit().putString(KEY_MIUIX_THEME, mode.name).apply()
    }

    /** The Material engine's light/dark choice. */
    fun loadMaterialTheme(context: Context): ThemeMode {
        val stored = prefs(context).getString(KEY_MATERIAL_THEME, null) ?: return ThemeMode.System
        return ThemeMode.entries.firstOrNull { it.name == stored } ?: ThemeMode.System
    }

    fun saveMaterialTheme(context: Context, mode: ThemeMode) {
        prefs(context).edit().putString(KEY_MATERIAL_THEME, mode.name).apply()
    }

    /** Whether the Material palette is taken from the wallpaper. */
    fun loadDynamicColor(context: Context): Boolean =
        prefs(context).getBoolean(KEY_DYNAMIC_COLOR, true)

    fun saveDynamicColor(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
    }

}
