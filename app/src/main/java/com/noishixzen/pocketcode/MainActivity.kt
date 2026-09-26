package com.noishixzen.pocketcode

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noishixzen.pocketcode.data.AppPreferences
import com.noishixzen.pocketcode.ui.MainViewModel
import com.noishixzen.pocketcode.ui.PocketDevApp
import com.noishixzen.pocketcode.ui.editor.ProjectEditorActivity
import com.noishixzen.pocketcode.ui.theme.PocketTheme

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appPrefs = AppPreferences(this)
        com.noishixzen.pocketcode.util.DisplayUtils.applyDisplayRefreshRate(this)

        handleTerminalIntent(intent)
        setContent {
            val state by vm.state.collectAsStateWithLifecycle()
            PocketTheme(
                themeMode = state.themeMode,
                amoledBlack = appPrefs.amoledBlack,
            ) {
                PocketDevApp(vm)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleTerminalIntent(intent)
    }

    private fun handleTerminalIntent(intent: Intent?) {
        val command = intent?.getStringExtra(ProjectEditorActivity.EXTRA_RUN_TERMINAL_COMMAND)
        if (!command.isNullOrBlank()) {
            vm.openProjectTerminal()
            vm.requestProjectTerminalCommand(command)
        }
    }
}
