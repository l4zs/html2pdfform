package de.l4zs.html2pdfform.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import de.l4zs.html2pdfform.extension.CopyContent
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration.Companion.seconds

class DesktopLogger(
    override var logLevel: Logger.LogLevel = Logger.LogLevel.INFO,
) : ViewModel(),
    Logger {
    private val _history = MutableStateFlow<List<LogEntry>>(listOf())
    private val history = _history.asStateFlow()

    private fun add(logEntry: LogEntry) {
        _history.value += logEntry
    }

    private fun remove(logEntry: LogEntry) {
        _history.value -= logEntry
    }

    override fun error(
        message: String,
        error: Throwable?,
    ) {
        add(LogEntry(Logger.LogLevel.ERROR, message, error))
    }

    override fun warn(
        message: String,
        error: Throwable?,
    ) {
        add(LogEntry(Logger.LogLevel.WARN, message, error))
    }

    override fun success(
        message: String,
        error: Throwable?,
    ) {
        add(LogEntry(Logger.LogLevel.SUCCESS, message, error))
    }

    override fun info(
        message: String,
        error: Throwable?,
    ) {
        add(LogEntry(Logger.LogLevel.INFO, message, error))
    }

    override fun debug(
        message: String,
        error: Throwable?,
    ) {
        add(LogEntry(Logger.LogLevel.DEBUG, message, error))
    }

    @Composable
    fun FloatingAlerts() {
        val logEntries by history.collectAsState()

        Box(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            AnimatedVisibility(
                visible = logEntries.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    logEntries.forEach { logEntry ->
                        key(logEntry.hashCode()) {
                            val isVisible =
                                remember { MutableTransitionState(false).apply { targetState = true } }

                            if (!isVisible.currentState && !isVisible.targetState) {
                                remove(logEntry)
                            }

                            AnimatedVisibility(
                                visibleState = isVisible,
                                enter = slideInVertically() + fadeIn(),
                                exit = slideOutVertically() + fadeOut(),
                            ) {
                                FloatingAlert(
                                    logEntry = logEntry,
                                    onDismiss = {
                                        isVisible.targetState = false
                                    },
                                )
                            }

                            LaunchedEffect(key1 = logEntry.hashCode()) {
                                when (logEntry.level) {
                                    Logger.LogLevel.ERROR,
                                    Logger.LogLevel.WARN,
                                    -> return@LaunchedEffect

                                    Logger.LogLevel.SUCCESS,
                                    Logger.LogLevel.INFO,
                                    Logger.LogLevel.DEBUG,
                                    -> {
                                        delay(5.seconds)
                                        isVisible.targetState = false
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun FloatingAlert(
        logEntry: LogEntry,
        onDismiss: () -> Unit,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .background(
                            when (logEntry.level) {
                                Logger.LogLevel.ERROR -> Color(0xFFD32F2F)
                                Logger.LogLevel.WARN -> Color(0xFFFFA000)
                                Logger.LogLevel.SUCCESS -> Color(0xFF388E3C)
                                Logger.LogLevel.INFO -> Color(0xFF1976D2)
                                Logger.LogLevel.DEBUG -> Color(0xFF7B1FA2)
                            }.copy(0.95f),
                            RoundedCornerShape(8.dp),
                        ).clickable { onDismiss() }
                        .pointerHoverIcon(PointerIcon.Hand)
                        .padding(16.dp)
                        .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector =
                        when (logEntry.level) {
                            Logger.LogLevel.ERROR -> Icons.Default.Error
                            Logger.LogLevel.WARN -> Icons.Default.Warning
                            Logger.LogLevel.SUCCESS -> Icons.Default.CheckCircle
                            Logger.LogLevel.INFO -> Icons.Default.Info
                            Logger.LogLevel.DEBUG -> Icons.Default.BugReport
                        },
                    contentDescription = "Success",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = logEntry.message,
                    color = Color.Black,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f),
                )
                if (logEntry.error != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    TooltipArea(
                        tooltip = {
                            Text(
                                "Kopiere den genauen Fehler",
                                color = Color.Black,
                                fontSize = 16.sp,
                                modifier =
                                    Modifier
                                        .background(Color.White, RoundedCornerShape(8.dp))
                                        .padding(8.dp, 4.dp),
                            )
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.CopyContent,
                            contentDescription = "Copy Error",
                            tint = Color.Black,
                            modifier =
                                Modifier
                                    .size(24.dp)
                                    .clickable {
                                        // TODO: Copy to clipboard
                                        println(logEntry.error.message)
                                    },
                        )
                    }
                }
            }
        }
    }

    data class LogEntry(
        val level: Logger.LogLevel,
        val message: String,
        val error: Throwable?,
        val time: Long = System.currentTimeMillis(),
    )
}
