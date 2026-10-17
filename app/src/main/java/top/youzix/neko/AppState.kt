/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import top.youzix.neko.ui.UiEngine
import top.youzix.neko.ui.predictiveback.PredictiveBackStyle

/**
 * Process-wide UI state for the shell.
 *
 * This build is the interface only: the four tabs draw themselves and remember how the user wants
 * them drawn, and nothing behind them does anything.
 */
object AppState {
    /** Which engine draws the app; persisted on change via [top.youzix.neko.ui.UiEnginePrefs]. */
    var engine by mutableStateOf(UiEngine.Miuix)

    /**
     * Translucent surfaces: the miuix bottom bar's liquid glass, and the Material 3 top bar's
     * blur. Persisted alongside the engine.
     */
    var useBlur by mutableStateOf(true)

    /** Which predictive-back animation plays on second-level pages. */
    var predictiveBackStyle by mutableStateOf(PredictiveBackStyle.Miuix)
}
