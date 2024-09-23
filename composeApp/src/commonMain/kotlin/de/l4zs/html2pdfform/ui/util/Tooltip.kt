package de.l4zs.html2pdfform.ui.util

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tooltip(
    text: String,
    content: @Composable () -> Unit,
) {
    TooltipArea(
        tooltip = {
            Text(
                text,
                color = Color.Black,
                fontSize = 16.sp,
                modifier =
                    Modifier
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(0.5.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(8.dp, 4.dp),
            )
        },
    ) {
        content()
    }
}
