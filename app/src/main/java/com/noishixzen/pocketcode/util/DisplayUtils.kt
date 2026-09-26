package com.noishixzen.pocketcode.util

import android.app.Activity
import android.os.Build
import com.noishixzen.pocketcode.data.AppPreferences

object DisplayUtils {
    fun applyDisplayRefreshRate(activity: Activity) {
        val prefs = AppPreferences(activity)
        val smooth120 = prefs.smooth120Hz
        runCatching {
            val window = activity.window ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (smooth120) {
                    val display = activity.display
                    val modes = display?.supportedModes
                    val highMode = modes?.maxByOrNull { it.refreshRate }
                    if (highMode != null) {
                        val lp = window.attributes
                        lp.preferredDisplayModeId = highMode.modeId
                        window.attributes = lp
                    }
                } else {
                    val lp = window.attributes
                    lp.preferredDisplayModeId = 0
                    window.attributes = lp
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (smooth120) {
                    @Suppress("DEPRECATION")
                    val modes = window.windowManager.defaultDisplay.supportedModes
                    val highMode = modes?.maxByOrNull { it.refreshRate }
                    if (highMode != null) {
                        val lp = window.attributes
                        lp.preferredDisplayModeId = highMode.modeId
                        window.attributes = lp
                    }
                } else {
                    val lp = window.attributes
                    lp.preferredDisplayModeId = 0
                    window.attributes = lp
                }
            }
        }
    }
}
