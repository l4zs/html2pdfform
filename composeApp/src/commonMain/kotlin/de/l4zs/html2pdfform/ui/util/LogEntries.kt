package de.l4zs.html2pdfform.ui.util

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.copy_stacktrace
import de.l4zs.html2pdfform.util.Logger
import de.l4zs.html2pdfform.util.Logger.LogEntry
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.seconds

/**
 *  Floating log entries that are displayed on top of the content like a snackbar.
 *  The entries are displayed in a column and are animated when they appear or disappear.
 *  They are sorted by their time of creation.
 *  Entries are removed after 5 seconds if they are of the level [Logger.LogLevel.SUCCESS].
 *  Other entries can be dismissed by clicking on them.
 *
 *  @receiver A [Logger] instance to listen for log entries.
 */
@Composable
fun Logger.FloatingEntries() {
    val logEntries by history.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize().padding(top = 8.dp).zIndex(Float.MAX_VALUE),
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
                logEntries.sortedByDescending { it.time }.forEach { logEntry ->
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
                            FloatingEntry(
                                logEntry = logEntry,
                                onDismiss = {
                                    isVisible.targetState = false
                                },
                            )
                        }

                        LaunchedEffect(key1 = logEntry.hashCode()) {
                            if (logEntry.level != Logger.LogLevel.SUCCESS) return@LaunchedEffect
                            delay(5.seconds)
                            isVisible.targetState = false
                        }
                    }
                }
            }
        }
    }
}

/**
 *  A single floating log entry that is displayed on top of the content like a snackbar.
 *  The entry is displayed in a row and is animated when it appears or disappears.
 *  The entry can be dismissed by clicking on it.
 *  If the entry has an error, a copy button is displayed to copy the stack trace to the clipboard.
 *  The background color of the entry depends on the log level.
 *  The entry is a clickable box that calls the [onDismiss] callback when clicked.
 *
 *  @param logEntry The log entry to display.
 *  @param onDismiss A callback that is called when the entry is dismissed.
 */
@Composable
private fun FloatingEntry(
    logEntry: LogEntry,
    onDismiss: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current

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
                            else -> Color(0xFF7B1FA2)
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
                        else -> Icons.Default.BugReport
                    },
                contentDescription = logEntry.level.name,
                tint = Color.Black,
                modifier =
                    Modifier
                        .size(24.dp),
            )
            Spacer(
                modifier =
                    Modifier
                        .width(12.dp),
            )
            Text(
                text =
                    logEntry.message +
                        if (logEntry.error != null) {
                            ": ${logEntry.error.message}"
                        } else {
                            ""
                        },
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            if (logEntry.error != null) {
                Spacer(
                    modifier =
                        Modifier
                            .width(12.dp),
                )
                Tooltip(
                    stringResource(Res.string.copy_stacktrace),
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = stringResource(Res.string.copy_stacktrace),
                        tint = Color.Black,
                        modifier =
                            Modifier
                                .size(24.dp)
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(logEntry.error.stackTraceToString()))
                                },
                    )
                }
            }
        }
    }
}
