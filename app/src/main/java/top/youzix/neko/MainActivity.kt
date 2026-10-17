/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import top.youzix.neko.ui.UiEngine
import top.youzix.neko.ui.UiEnginePrefs
import top.youzix.neko.ui.material3.MaterialApp
import top.youzix.neko.ui.miuix.MiuixApp

/**
 * Picks the UI engine and hands the whole window to it.
 *
 * Two complete implementations of the same four tabs live side by side — [MiuixApp] and
 * [MaterialApp] — and the choice is persisted, so switching on the About page takes effect
 * immediately.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AppState.engine = UiEnginePrefs.load(this)
        AppState.useBlur = UiEnginePrefs.loadUseBlur(this)
        AppState.predictiveBackStyle = UiEnginePrefs.loadPredictiveBackStyle(this)
        AppState.debugMode = UiEnginePrefs.loadDebugMode(this)
        setContent {
            when (AppState.engine) {
                UiEngine.Miuix -> MiuixApp()
                UiEngine.Material3 -> MaterialApp()
            }
        }
    }
}
