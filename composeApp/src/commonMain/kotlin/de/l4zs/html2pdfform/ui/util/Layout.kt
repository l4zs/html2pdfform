package de.l4zs.html2pdfform.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TwoColumnLayout(
    leftColumnWeight: Float = 1f,
    rightColumnWeight: Float = 1f,
    leftColumn: @Composable () -> Unit,
    rightColumn: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
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
    rightColumn: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
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
    bottomRow: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            topRow()
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            bottomRow()
        }
    }
}
