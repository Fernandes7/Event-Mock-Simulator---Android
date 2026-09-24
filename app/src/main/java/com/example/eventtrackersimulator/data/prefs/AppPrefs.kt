package com.example.eventtrackersimulator.data.prefs

import android.content.Context
import androidx.core.content.edit
import com.example.eventtrackersimulator.common.Constants

class AppPrefs(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(
        Constants.PREFS_NAME,
        Context.MODE_PRIVATE,
    )

    fun isInstallTracked(): Boolean =
        prefs.getBoolean(Constants.PREF_KEY_INSTALL_TRACKED, false)

    fun setInstallTracked(tracked: Boolean) {
        prefs.edit { putBoolean(Constants.PREF_KEY_INSTALL_TRACKED, tracked) }
    }
}
