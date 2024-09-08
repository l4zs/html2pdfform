package de.l4zs.html2pdfform.ui.setting

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TwoColumnLayout(
    leftColumnWeight: Float = 1f,
    rightColumnWeight: Float = 1f,
    leftColumn: @Composable () -> Unit,
    rightColumn: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.weight(leftColumnWeight)) {
            leftColumn()
        }
        Box(modifier = Modifier.weight(rightColumnWeight)) {
            rightColumn()
        }
    }
}

@Composable
fun ThreeColumnLayout(
    leftColumnWeight: Float = 1f,
    middleColumnWeight: Float = 1f,
    rightColumnWeight: Float = 1f,
    leftColumn: @Composable () -> Unit,
    middleColumn: @Composable () -> Unit,
    rightColumn: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.weight(leftColumnWeight)) {
            leftColumn()
        }
        Box(modifier = Modifier.weight(middleColumnWeight)) {
            middleColumn()
        }
        Box(modifier = Modifier.weight(rightColumnWeight)) {
            rightColumn()
        }
    }
}

@Composable
fun TwoRowLayout(
    topRow: @Composable () -> Unit,
    bottomRow: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            topRow()
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            bottomRow()
        }
    }
}
