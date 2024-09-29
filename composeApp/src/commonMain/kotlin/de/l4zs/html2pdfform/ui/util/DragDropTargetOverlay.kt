package de.l4zs.html2pdfform.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.drag_and_drop_here
import org.jetbrains.compose.resources.stringResource

/**
 * Overlay that is displayed on top of the content when a drag and drop operation is in progress.
 * The overlay will be displayed in the same size as the content and will show a message and an icon.
 *
 * @param isDropAllowed Whether the current drop operation is allowed.
 * @param modifier The modifier to apply to the overlay.
 * @param content The content to display under the overlay.
 */
@Composable
fun DragDropTargetOverlay(
    isDropAllowed: Boolean,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    // Remember the size of the content to make the overlay the same size.
    val contentSize = remember { mutableStateOf(IntSize.Zero) }

    Box(modifier) {
        Box(
            modifier =
                Modifier.onSizeChanged { size ->
                    contentSize.value = size
                },
        ) {
            content()
        }

        if (isDropAllowed) {
            Box(
                modifier =
                    Modifier
                        .size(contentSize.value.width.dp, contentSize.value.height.dp)
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.3f))
                        .border(2.dp, MaterialTheme.colors.primary),
            ) {
                Icon(
                    imageVector = Icons.Filled.FileUpload,
                    contentDescription = stringResource(Res.string.drag_and_drop_here),
                    tint = Color.White,
                    modifier =
                        Modifier
                            .size(
                                64
                                    .dp
                                    .coerceAtMost(contentSize.value.height.dp - 10.dp)
                                    .coerceAtMost(contentSize.value.width.dp - 10.dp),
                            ).align(Alignment.Center),
                )
            }
        }
    }
}
