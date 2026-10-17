/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package top.youzix.neko.demo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * The web tab's address bar.
 *
 * There is no browser behind it: [submit] only records what was typed and the card below says the
 * view is not part of this build. The state lives here because the tab is disposed every time the
 * pager scrolls away from it.
 */
object WebShell {

    /** What is in the address field right now. */
    var address by mutableStateOf("")
        private set

    /** The address that was submitted, or an empty string while nothing has been. */
    var page by mutableStateOf("")
        private set

    /** No history exists, so both arrows stay grey; the properties keep the screens unchanged. */
    val canGoBack: Boolean = false
    val canGoForward: Boolean = false

    fun onAddressChange(value: String) {
        address = value
    }

    fun submit() {
        val typed = address.trim()
        if (typed.isEmpty()) {
            page = ""
            return
        }
        val url = if (typed.contains("://")) typed else "https://$typed"
        address = url
        page = url
    }

    fun reload() = Unit

    fun goBack() = Unit

    fun goForward() = Unit

    /** Back to the start state. */
    fun reset() {
        address = ""
        page = ""
    }
}
