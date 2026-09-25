package com.noishixzen.pocketcode.ui.editor

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.Javascript
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.noishixzen.pocketcode.MainActivity
import com.noishixzen.pocketcode.ui.theme.AppThemeMode
import com.noishixzen.pocketcode.ui.theme.OrangeGradient
import com.noishixzen.pocketcode.ui.theme.PocketOrange
import com.noishixzen.pocketcode.ui.theme.PocketOrangeAmber
import com.noishixzen.pocketcode.ui.theme.PocketOrangeDeep
import com.noishixzen.pocketcode.ui.theme.PocketTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

data class EditorFileItem(
    val relativePath: String,
    val name: String,
    val isDirectory: Boolean,
    val sizeBytes: Long = 0L,
    val depth: Int = 0,
)

class ProjectEditorActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PROJECT_ID = "extra_project_id"
        const val EXTRA_PROJECT_NAME = "extra_project_name"
        const val EXTRA_PROJECT_ROOT_PATH = "extra_project_root_path"
        const val EXTRA_INITIAL_FILE_PATH = "extra_initial_file_path"
        const val EXTRA_THEME_MODE = "extra_theme_mode"
        const val EXTRA_RUN_TERMINAL_COMMAND = "RUN_TERMINAL_COMMAND"

        fun createIntent(
            context: Context,
            projectId: String,
            projectName: String,
            projectRootPath: String = "",
            initialFilePath: String = "",
            themeMode: AppThemeMode = AppThemeMode.DARK,
        ): Intent {
            return Intent(context, ProjectEditorActivity::class.java).apply {
                putExtra(EXTRA_PROJECT_ID, projectId)
                putExtra(EXTRA_PROJECT_NAME, projectName)
                putExtra(EXTRA_PROJECT_ROOT_PATH, projectRootPath)
                putExtra(EXTRA_INITIAL_FILE_PATH, initialFilePath)
                putExtra(EXTRA_THEME_MODE, themeMode.name)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID) ?: ""
        val projectName = intent.getStringExtra(EXTRA_PROJECT_NAME) ?: "Project"
        val projectRootPath = intent.getStringExtra(EXTRA_PROJECT_ROOT_PATH) ?: ""
        val initialFilePath = intent.getStringExtra(EXTRA_INITIAL_FILE_PATH) ?: ""
        val themeModeStr = intent.getStringExtra(EXTRA_THEME_MODE) ?: AppThemeMode.DARK.name
        val themeMode = runCatching { AppThemeMode.valueOf(themeModeStr) }.getOrDefault(AppThemeMode.DARK)

        val workspaceDir = resolveWorkspaceDirectory(projectId, projectRootPath)

        setContent {
            PocketTheme(themeMode = themeMode) {
                ProjectEditorScreen(
                    projectId = projectId,
                    projectName = projectName,
                    workspaceDir = workspaceDir,
                    initialFilePath = initialFilePath,
                    onBack = { finish() },
                    onConnectTerminal = { command ->
                        val launchMain = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            putExtra(EXTRA_RUN_TERMINAL_COMMAND, command)
                        }
                        startActivity(launchMain)
                    },
                )
            }
        }
    }

    private fun resolveWorkspaceDirectory(projectId: String, rootPath: String): File {
        val base = File(filesDir, "workspaces/$projectId").apply { mkdirs() }.canonicalFile
        if (rootPath.isBlank()) return base
        val selected = File(base, rootPath).canonicalFile
        return if (runCatching { selected.toPath().startsWith(base.toPath()) }.getOrDefault(false)) {
            selected.apply { mkdirs() }
        } else {
            base
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectEditorScreen(
    projectId: String,
    projectName: String,
    workspaceDir: File,
    initialFilePath: String = "",
    onBack: () -> Unit,
    onConnectTerminal: (String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var files by remember { mutableStateOf<List<EditorFileItem>>(emptyList()) }
    var currentFile by rememberSaveable { mutableStateOf(initialFilePath) }
    var editorText by remember { mutableStateOf(TextFieldValue("")) }
    var originalSavedText by remember { mutableStateOf("") }
    var isModified by remember { mutableStateOf(false) }
    var isFileLoading by remember { mutableStateOf(false) }

    // Undo / Redo
    var undoStack by remember { mutableStateOf<List<TextFieldValue>>(emptyList()) }
    var redoStack by remember { mutableStateOf<List<TextFieldValue>>(emptyList()) }

    // Modals / Sheets
    var showFileDrawer by rememberSaveable { mutableStateOf(false) }
    var showNewFileDialog by rememberSaveable { mutableStateOf(false) }
    var showNewFolderDialog by rememberSaveable { mutableStateOf(false) }
    var showRunConnectSheet by rememberSaveable { mutableStateOf(false) }
    var showUnsavedDialog by rememberSaveable { mutableStateOf(false) }
    var pendingSwitchFile by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by rememberSaveable { mutableStateOf<String?>(null) }
    var showSearchRow by rememberSaveable { mutableStateOf(false) }
    var searchKeyword by rememberSaveable { mutableStateOf("") }
    var saveSuccessFeedback by remember { mutableStateOf(false) }

    fun refreshFileList() {
        if (!workspaceDir.isDirectory) {
            files = emptyList()
            return
        }
        val items = mutableListOf<EditorFileItem>()
        val ignoredNames = setOf(".git", ".pocketdev", ".claude", "node_modules", ".gradle", "build", ".cxx", ".idea", "__pycache__")

        workspaceDir.walkTopDown()
            .maxDepth(8)
            .onEnter { dir ->
                dir == workspaceDir || (dir.name !in ignoredNames && !dir.name.startsWith("."))
            }
            .filter { it != workspaceDir }
            .forEach { file ->
                val rel = file.relativeTo(workspaceDir).invariantSeparatorsPath
                val depth = rel.count { it == '/' }
                items.add(
                    EditorFileItem(
                        relativePath = rel,
                        name = file.name,
                        isDirectory = file.isDirectory,
                        sizeBytes = if (file.isFile) file.length() else 0L,
                        depth = depth,
                    )
                )
            }
        files = items.sortedWith(compareBy({ !it.isDirectory }, { it.relativePath.lowercase() }))
    }

    fun loadFileContent(relativePath: String) {
        if (relativePath.isBlank()) return
        val target = File(workspaceDir, relativePath)
        if (!target.isFile) return

        isFileLoading = true
        scope.launch(Dispatchers.IO) {
            val content = runCatching {
                if (target.length() > 2_000_000L) {
                    target.bufferedReader().use { r ->
                        val chars = CharArray(1_000_000)
                        val read = r.read(chars)
                        String(chars, 0, read) + "\n\n[File truncated for preview]"
                    }
                } else {
                    target.readText()
                }
            }.getOrElse { "Could not load file: ${it.message}" }

            withContext(Dispatchers.Main) {
                currentFile = relativePath
                editorText = TextFieldValue(content, TextRange(0))
                originalSavedText = content
                isModified = false
                undoStack = emptyList()
                redoStack = emptyList()
                isFileLoading = false
            }
        }
    }

    fun saveFile() {
        if (currentFile.isBlank()) return
        val target = File(workspaceDir, currentFile)
        scope.launch(Dispatchers.IO) {
            runCatching {
                target.parentFile?.mkdirs()
                target.writeText(editorText.text)
                withContext(Dispatchers.Main) {
                    originalSavedText = editorText.text
                    isModified = false
                    saveSuccessFeedback = true
                    Toast.makeText(context, "Saved ${target.name}", Toast.LENGTH_SHORT).show()
                }
                delay(1800)
                withContext(Dispatchers.Main) {
                    saveSuccessFeedback = false
                }
            }.onFailure { err ->
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error saving: ${err.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun onTextChange(newVal: TextFieldValue) {
        if (newVal.text != editorText.text) {
            if (undoStack.size > 40) {
                undoStack = undoStack.drop(1) + editorText
            } else {
                undoStack = undoStack + editorText
            }
            redoStack = emptyList()
            isModified = newVal.text != originalSavedText
        }
        editorText = newVal
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val prev = undoStack.last()
            undoStack = undoStack.dropLast(1)
            redoStack = redoStack + editorText
            editorText = prev
            isModified = prev.text != originalSavedText
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.last()
            redoStack = redoStack.dropLast(1)
            undoStack = undoStack + editorText
            editorText = next
            isModified = next.text != originalSavedText
        }
    }

    fun insertSymbol(symbol: String) {
        val selStart = editorText.selection.start.coerceAtLeast(0)
        val selEnd = editorText.selection.end.coerceAtLeast(0)
        val before = editorText.text.substring(0, selStart)
        val after = editorText.text.substring(selEnd)
        val newText = before + symbol + after
        val newCursor = selStart + symbol.length
        onTextChange(TextFieldValue(newText, TextRange(newCursor)))
    }

    // Initial load
    LaunchedEffect(Unit) {
        refreshFileList()
        if (initialFilePath.isNotBlank()) {
            loadFileContent(initialFilePath)
        } else {
            // Find first code or entry file
            val candidate = files.firstOrNull {
                !it.isDirectory && (
                    it.name.endsWith(".py") || it.name.endsWith(".js") ||
                    it.name.endsWith(".ts") || it.name.endsWith(".html") ||
                    it.name.endsWith(".kt") || it.name.endsWith(".json") ||
                    it.name.endsWith(".md")
                )
            } ?: files.firstOrNull { !it.isDirectory }
            candidate?.let { loadFileContent(it.relativePath) }
        }
    }

    BackHandler {
        if (isModified) {
            pendingSwitchFile = null
            showUnsavedDialog = true
        } else {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.clickable { showFileDrawer = true }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (currentFile.isNotBlank()) currentFile.substringAfterLast('/') else "Select File",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (isModified) {
                                Spacer(Modifier.width(6.dp))
                                Box(
                                    Modifier
                                        .size(8.dp)
                                        .background(PocketOrange, CircleShape)
                                )
                            }
                        }
                        Text(
                            text = if (currentFile.isNotBlank()) currentFile else projectName,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isModified) {
                            pendingSwitchFile = null
                            showUnsavedDialog = true
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to Project")
                    }
                },
                actions = {
                    // Files explorer toggle
                    IconButton(onClick = { showFileDrawer = true }) {
                        Icon(Icons.Default.FolderOpen, "Project Files", tint = MaterialTheme.colorScheme.onSurface)
                    }

                    // Undo
                    IconButton(onClick = { undo() }, enabled = undoStack.isNotEmpty()) {
                        Icon(
                            Icons.AutoMirrored.Filled.Undo,
                            "Undo",
                            tint = if (undoStack.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        )
                    }

                    // Redo
                    IconButton(onClick = { redo() }, enabled = redoStack.isNotEmpty()) {
                        Icon(
                            Icons.AutoMirrored.Filled.Redo,
                            "Redo",
                            tint = if (redoStack.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        )
                    }

                    // Search in file
                    IconButton(onClick = { showSearchRow = !showSearchRow }) {
                        Icon(Icons.Default.Search, "Search text", tint = if (showSearchRow) PocketOrange else MaterialTheme.colorScheme.onSurface)
                    }

                    // Save Button
                    IconButton(
                        onClick = { saveFile() },
                        enabled = currentFile.isNotBlank(),
                    ) {
                        if (saveSuccessFeedback) {
                            Icon(Icons.Default.Check, "Saved", tint = Color(0xFF4ADE80))
                        } else {
                            Icon(
                                Icons.Default.Save,
                                "Save File",
                                tint = if (isModified) PocketOrange else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }

                    // Run & Connect Button (Gradient Accent)
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(36.dp)
                            .background(OrangeGradient, RoundedCornerShape(10.dp))
                            .clickable { showRunConnectSheet = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.PlayArrow, "Run & Connect", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        bottomBar = {
            // Quick Code Accessory Bar (Sticky above keyboard)
            CodeKeyboardAccessoryBar(
                onInsertSymbol = { insertSymbol(it) },
                onSave = { saveFile() },
                onRun = { showRunConnectSheet = true },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding(),
        ) {
            // Search Bar
            AnimatedVisibility(showSearchRow) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(8.dp))
                        BasicTextField(
                            value = searchKeyword,
                            onValueChange = { searchKeyword = it },
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                        if (searchKeyword.isNotEmpty()) {
                            val count = if (searchKeyword.length >= 2) editorText.text.split(searchKeyword).size - 1 else 0
                            Text(
                                text = "$count matches",
                                fontSize = 11.sp,
                                color = PocketOrange,
                                modifier = Modifier.padding(horizontal = 6.dp),
                            )
                            IconButton(onClick = { searchKeyword = "" }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Clear, "Clear search", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Status bar info (Lines, Column, Encoding)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val linesCount = editorText.text.count { it == '\n' } + 1
                    val charCount = editorText.text.length
                    val ext = currentFile.substringAfterLast('.', "txt").uppercase()
                    Text(
                        text = "$ext  •  $linesCount lines  •  $charCount chars",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isModified) {
                            Text(
                                "Unsaved changes",
                                fontSize = 11.sp,
                                color = PocketOrange,
                                fontWeight = FontWeight.SemiBold,
                            )
                        } else {
                            Text(
                                "Saved",
                                fontSize = 11.sp,
                                color = Color(0xFF4ADE80),
                            )
                        }
                    }
                }
            }

            // Code Editor Canvas
            if (isFileLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PocketOrange)
                }
            } else if (currentFile.isBlank()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Code, null, modifier = Modifier.size(56.dp), tint = PocketOrange.copy(alpha = 0.6f))
                        Spacer(Modifier.height(12.dp))
                        Text("No file open", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Spacer(Modifier.height(6.dp))
                        Text("Choose a file from the project or create one.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { showFileDrawer = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PocketOrange),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Icon(Icons.Default.FolderOpen, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Browse Files")
                        }
                    }
                }
            } else {
                CodeEditorCanvas(
                    textFieldValue = editorText,
                    onValueChange = { onTextChange(it) },
                    searchKeyword = searchKeyword,
                )
            }
        }
    }

    // File Drawer / BottomSheet
    if (showFileDrawer) {
        ModalBottomSheet(
            onDismissRequest = { showFileDrawer = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(horizontal = 18.dp, vertical = 8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Project Files", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(projectName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row {
                        IconButton(onClick = { showNewFolderDialog = true }) {
                            Icon(Icons.Default.CreateNewFolder, "New Folder", tint = PocketOrange)
                        }
                        IconButton(onClick = { showNewFileDialog = true }) {
                            Icon(Icons.Default.Add, "New File", tint = PocketOrange)
                        }
                        IconButton(onClick = { refreshFileList() }) {
                            Icon(Icons.Default.Refresh, "Refresh Files")
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                if (files.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No files in project. Tap + to create one.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(files, key = { it.relativePath }) { item ->
                            val isSelected = item.relativePath == currentFile
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) PocketOrange.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable {
                                        if (!item.isDirectory) {
                                            if (isModified && item.relativePath != currentFile) {
                                                pendingSwitchFile = item.relativePath
                                                showUnsavedDialog = true
                                            } else {
                                                loadFileContent(item.relativePath)
                                                showFileDrawer = false
                                            }
                                        }
                                    }
                                    .padding(
                                        start = (12 + item.depth * 14).dp,
                                        end = 12.dp,
                                        top = 8.dp,
                                        bottom = 8.dp,
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val icon = getFileIcon(item)
                                val iconTint = if (item.isDirectory) PocketOrangeAmber else getFileColor(item.name)
                                Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = item.name,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = if (isSelected || item.isDirectory) FontWeight.SemiBold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (isSelected) PocketOrange else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                if (!item.isDirectory) {
                                    IconButton(
                                        onClick = { showDeleteDialog = item.relativePath },
                                        modifier = Modifier.size(24.dp),
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            "Delete",
                                            modifier = Modifier.size(15.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // New File Dialog
    if (showNewFileDialog) {
        var newFileName by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewFileDialog = false },
            title = { Text("Create New File") },
            text = {
                Column {
                    Text("Enter file name or relative path:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        placeholder = { Text("e.g. main.py, index.html, style.css") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PocketOrange),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = newFileName.trim()
                        if (clean.isNotBlank()) {
                            val target = File(workspaceDir, clean)
                            target.parentFile?.mkdirs()
                            if (!target.exists()) target.createNewFile()
                            showNewFileDialog = false
                            refreshFileList()
                            loadFileContent(clean)
                            showFileDrawer = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PocketOrange),
                ) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showNewFileDialog = false }) { Text("Cancel") }
            },
        )
    }

    // New Folder Dialog
    if (showNewFolderDialog) {
        var newFolderName by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewFolderDialog = false },
            title = { Text("Create New Folder") },
            text = {
                Column {
                    Text("Enter folder name or relative path:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        placeholder = { Text("e.g. src, static, assets") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PocketOrange),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = newFolderName.trim()
                        if (clean.isNotBlank()) {
                            val target = File(workspaceDir, clean)
                            target.mkdirs()
                            showNewFolderDialog = false
                            refreshFileList()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PocketOrange),
                ) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showNewFolderDialog = false }) { Text("Cancel") }
            },
        )
    }

    // Delete confirmation
    showDeleteDialog?.let { fileToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete ${fileToDelete.substringAfterLast('/')}?") },
            text = { Text("This will permanently remove this file from the project.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val target = File(workspaceDir, fileToDelete)
                        target.deleteRecursively()
                        showDeleteDialog = null
                        if (currentFile == fileToDelete) {
                            currentFile = ""
                            editorText = TextFieldValue("")
                            isModified = false
                        }
                        refreshFileList()
                    },
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") }
            },
        )
    }

    // Unsaved changes dialog
    if (showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedDialog = false },
            title = { Text("Unsaved Changes") },
            text = { Text("You have unsaved changes in ${currentFile.substringAfterLast('/')}. Do you want to save before leaving?") },
            confirmButton = {
                Button(
                    onClick = {
                        saveFile()
                        showUnsavedDialog = false
                        val target = pendingSwitchFile
                        if (target != null) {
                            pendingSwitchFile = null
                            loadFileContent(target)
                            showFileDrawer = false
                        } else {
                            onBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PocketOrange),
                ) { Text("Save & Continue") }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            showUnsavedDialog = false
                            isModified = false
                            val target = pendingSwitchFile
                            if (target != null) {
                                pendingSwitchFile = null
                                loadFileContent(target)
                                showFileDrawer = false
                            } else {
                                onBack()
                            }
                        },
                    ) { Text("Discard", color = MaterialTheme.colorScheme.error) }
                    Spacer(Modifier.width(6.dp))
                    TextButton(onClick = { showUnsavedDialog = false }) { Text("Cancel") }
                }
            },
        )
    }

    // Run & Connect Modal Bottom Sheet
    if (showRunConnectSheet) {
        RunConnectModalSheet(
            currentFile = currentFile,
            workspaceDir = workspaceDir,
            onDismiss = { showRunConnectSheet = false },
            onConnectTerminal = { cmd ->
                showRunConnectSheet = false
                onConnectTerminal(cmd)
            },
        )
    }
}

@Composable
private fun CodeEditorCanvas(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    searchKeyword: String,
) {
    val hScrollState = rememberScrollState()
    val vScrollState = rememberScrollState()
    val lines = remember(textFieldValue.text) { textFieldValue.text.lines() }
    val lineCount = lines.size

    val editorBg = if (androidx.compose.foundation.isSystemInDarkTheme()) Color(0xFF0D1017) else Color(0xFFFAFAFA)
    val gutterBg = if (androidx.compose.foundation.isSystemInDarkTheme()) Color(0xFF131720) else Color(0xFFF0F2F5)
    val textColor = if (androidx.compose.foundation.isSystemInDarkTheme()) Color(0xFFE2E8F0) else Color(0xFF1E293B)
    val gutterTextColor = if (androidx.compose.foundation.isSystemInDarkTheme()) Color(0xFF64748B) else Color(0xFF94A3B8)

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(editorBg)
            .verticalScroll(vScrollState),
    ) {
        // Line Numbers Gutter
        Column(
            modifier = Modifier
                .width(46.dp)
                .background(gutterBg)
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.End,
        ) {
            for (i in 1..lineCount) {
                Text(
                    text = "$i",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.5.sp,
                    lineHeight = 20.sp,
                    color = gutterTextColor,
                    maxLines = 1,
                )
            }
        }

        Spacer(Modifier.width(6.dp))

        // Editor input
        Box(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(hScrollState)
                .padding(vertical = 12.dp, horizontal = 6.dp),
        ) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = textColor,
                ),
                cursorBrush = SolidColor(PocketOrange),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Default,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CodeKeyboardAccessoryBar(
    onInsertSymbol: (String) -> Unit,
    onSave: () -> Unit,
    onRun: () -> Unit,
) {
    val symbols = listOf(
        "Tab" to "  ",
        "{" to "{",
        "}" to "}",
        "(" to "(",
        ")" to ")",
        "[" to "[",
        "]" to "]",
        "<" to "<",
        ">" to ">",
        "/" to "/",
        "\\" to "\\",
        "=" to "=",
        "\"" to "\"",
        "'" to "'",
        ";" to ";",
        ":" to ":",
        "_" to "_",
        "-" to "-",
        "+" to "+",
        "*" to "*",
        "&" to "&",
        "|" to "|",
        "!" to "!",
        "$" to "$",
        "#" to "#",
        "%" to "%",
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Quick Save Chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PocketOrange.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, PocketOrange.copy(alpha = 0.3f)),
                modifier = Modifier.clickable(onClick = onSave),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Save, null, tint = PocketOrange, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Save", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PocketOrange)
                }
            }

            // Quick Run Chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PocketOrangeDeep.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, PocketOrangeDeep.copy(alpha = 0.3f)),
                modifier = Modifier.clickable(onClick = onRun),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.PlayArrow, null, tint = PocketOrangeDeep, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Run", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PocketOrangeDeep)
                }
            }

            // Symbol Chips
            symbols.forEach { (label, value) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable { onInsertSymbol(value) },
                ) {
                    Text(
                        text = label,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RunConnectModalSheet(
    currentFile: String,
    workspaceDir: File,
    onDismiss: () -> Unit,
    onConnectTerminal: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val ext = currentFile.substringAfterLast('.', "").lowercase()

    val defaultCmd = remember(currentFile) {
        when (ext) {
            "py" -> "python3 ${currentFile.ifBlank { "main.py" }}"
            "js" -> "node ${currentFile.ifBlank { "index.js" }}"
            "ts" -> "npx ts-node ${currentFile.ifBlank { "index.ts" }}"
            "sh", "bash" -> "bash ${currentFile.ifBlank { "run.sh" }}"
            "html" -> "open-preview"
            else -> "ls -la"
        }
    }

    var commandInput by rememberSaveable { mutableStateOf(defaultCmd) }
    var consoleOutput by remember { mutableStateOf<List<String>>(emptyList()) }
    var isRunningProcess by remember { mutableStateOf(false) }

    fun executeQuickCommand() {
        if (commandInput.isBlank()) return
        isRunningProcess = true
        consoleOutput = listOf("$ " + commandInput, "Executing...")

        scope.launch(Dispatchers.IO) {
            try {
                val pb = ProcessBuilder("/system/bin/sh", "-c", commandInput)
                pb.directory(workspaceDir)
                pb.redirectErrorStream(true)
                val proc = pb.start()

                val reader = BufferedReader(InputStreamReader(proc.inputStream))
                val lines = mutableListOf<String>()
                lines.add("$ " + commandInput)
                var line: String? = reader.readLine()
                while (line != null) {
                    lines.add(line)
                    line = reader.readLine()
                }
                val exitCode = proc.waitFor()
                lines.add("\n[Process completed with exit code $exitCode]")

                withContext(Dispatchers.Main) {
                    consoleOutput = lines
                    isRunningProcess = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    consoleOutput = listOf("$ " + commandInput, "Error executing command: ${e.message}")
                    isRunningProcess = false
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 18.dp, vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(OrangeGradient, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Run & Connect", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(currentFile.ifBlank { "Project" }, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
            }

            Spacer(Modifier.height(12.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = PocketOrange,
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Command & Console", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Live Web Preview", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                )
            }

            Spacer(Modifier.height(14.dp))

            if (selectedTab == 0) {
                // Command & Console Tab
                OutlinedTextField(
                    value = commandInput,
                    onValueChange = { commandInput = it },
                    label = { Text("Execution Command") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PocketOrange),
                    trailingIcon = {
                        IconButton(onClick = { executeQuickCommand() }, enabled = !isRunningProcess) {
                            if (isRunningProcess) {
                                CircularProgressIndicator(Modifier.size(18.dp), color = PocketOrange, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.PlayArrow, "Execute", tint = PocketOrange)
                            }
                        }
                    },
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = { executeQuickCommand() },
                        enabled = !isRunningProcess,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PocketOrange),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Run in Console")
                    }

                    OutlinedButton(
                        onClick = { onConnectTerminal(commandInput) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(Icons.Default.Terminal, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Connect PRoot Terminal", fontSize = 12.sp, maxLines = 1)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Console output screen
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    color = Color(0xFF0A0D12),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF242A38)),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        if (consoleOutput.isEmpty()) {
                            item {
                                Text(
                                    "Ready to execute. Tap 'Run in Console' or 'Connect PRoot Terminal'.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                )
                            }
                        } else {
                            items(consoleOutput) { line ->
                                Text(
                                    text = line,
                                    color = if (line.startsWith("$")) PocketOrangeAmber else if ("error" in line.lowercase() || "failed" in line.lowercase()) Color(0xFFF87171) else Color(0xFFE2E8F0),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                )
                            }
                        }
                    }
                }
            } else {
                // Live Web Preview Tab
                LiveWebPreviewTab(
                    workspaceDir = workspaceDir,
                    currentFile = currentFile,
                )
            }
        }
    }
}

@Composable
private fun LiveWebPreviewTab(
    workspaceDir: File,
    currentFile: String,
) {
    var previewUrl by rememberSaveable { mutableStateOf("http://127.0.0.1:3000") }
    var loadLocalFile by rememberSaveable { mutableStateOf(currentFile.endsWith(".html")) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = previewUrl,
                onValueChange = { previewUrl = it; loadLocalFile = false },
                label = { Text("Preview URL / Port") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PocketOrange),
            )
            IconButton(onClick = { webViewInstance?.reload() }) {
                Icon(Icons.Default.Refresh, "Reload")
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            listOf("3000", "5173", "8000", "8080").forEach { port ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clickable {
                        previewUrl = "http://127.0.0.1:$port"
                        loadLocalFile = false
                        webViewInstance?.loadUrl(previewUrl)
                    },
                ) {
                    Text(":$port", fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            if (File(workspaceDir, "index.html").exists() || currentFile.endsWith(".html")) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PocketOrange.copy(alpha = 0.15f),
                    modifier = Modifier.clickable {
                        loadLocalFile = true
                        val htmlFile = if (currentFile.endsWith(".html")) File(workspaceDir, currentFile) else File(workspaceDir, "index.html")
                        webViewInstance?.loadUrl("file://${htmlFile.absolutePath}")
                    },
                ) {
                    Text("Direct HTML", color = PocketOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewInstance = this
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        webViewClient = WebViewClient()
                        if (loadLocalFile) {
                            val htmlFile = if (currentFile.endsWith(".html")) File(workspaceDir, currentFile) else File(workspaceDir, "index.html")
                            if (htmlFile.exists()) {
                                loadUrl("file://${htmlFile.absolutePath}")
                            } else {
                                loadDataWithBaseURL(null, "<h3>No index.html found</h3>", "text/html", "UTF-8", null)
                            }
                        } else {
                            loadUrl(previewUrl)
                        }
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private fun getFileIcon(item: EditorFileItem): ImageVector {
    if (item.isDirectory) return Icons.Default.Folder
    val ext = item.name.substringAfterLast('.', "").lowercase()
    return when (ext) {
        "html", "htm" -> Icons.Default.Html
        "js", "ts", "jsx", "tsx" -> Icons.Default.Javascript
        "py" -> Icons.Default.Terminal
        "c", "cpp", "h", "hpp" -> Icons.Default.Code
        "json", "yaml", "yml", "xml" -> Icons.Default.Description
        else -> Icons.Default.Description
    }
}

private fun getFileColor(fileName: String): Color {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    return when (ext) {
        "py" -> Color(0xFFFBBF24)
        "js", "jsx" -> Color(0xFFFACC15)
        "ts", "tsx" -> Color(0xFF38BDF8)
        "html" -> Color(0xFFFF5722)
        "css", "scss" -> Color(0xFF818CF8)
        "json" -> Color(0xFF4ADE80)
        "md" -> Color(0xFF94A3B8)
        "kt", "java" -> Color(0xFFA78BFA)
        else -> Color(0xFF94A3B8)
    }
}
