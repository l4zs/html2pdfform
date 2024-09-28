package de.l4zs.html2pdfform.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.copied
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.seconds

@Composable
fun CopyText(
    text: String,
    copy: String,
) {
    val clipboardManager = LocalClipboardManager.current
    var showConfirmation by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier =
            Modifier
                .pointerInput(Unit) {
                    detectTapGestures { position ->
                        offsetX = position.x
                        offsetY = position.y
                        clipboardManager.setText(AnnotatedString(copy))
                        showConfirmation = true
                        coroutineScope.launch {
                            delay(2.seconds)
                            showConfirmation = false
                        }
                    }
                },
    ) {
        Text(
            text = text,
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            color = MaterialTheme.colors.primary,
        )

        AnimatedVisibility(
            visible = showConfirmation,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.absoluteOffset(offsetX.dp + 10.dp, offsetY.dp - 30.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colors.onSurface.copy(alpha = 0.9f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = stringResource(Res.string.copied),
                    color = MaterialTheme.colors.surface,
                    fontSize = 12.sp,
                )
            }
        }
    }
}
