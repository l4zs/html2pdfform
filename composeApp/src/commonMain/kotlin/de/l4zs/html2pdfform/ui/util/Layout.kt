package de.l4zs.html2pdfform.ui.util

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A simple layout that displays two columns. The columns are spaced by
 * 16dp. The columns will take up the same amount of space by default.
 * To change the weight of the columns, use the [leftColumnWeight] and
 * [rightColumnWeight] parameters.
 *
 * @param leftColumnWeight The weight of the left column. Defaults to 1f.
 * @param rightColumnWeight The weight of the right column. Defaults to 1f.
 * @param leftColumn The content of the left column.
 * @param rightColumn The content of the right column.
 */
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

/**
 * A simple layout that displays three columns. The columns are spaced by
 * 16dp. The columns will take up the same amount of space by default.
 * To change the weight of the columns, use the [leftColumnWeight],
 * [middleColumnWeight], and [rightColumnWeight] parameters.
 *
 * @param leftColumnWeight The weight of the left column. Defaults to 1f.
 * @param middleColumnWeight The weight of the middle column. Defaults to
 *    1f.
 * @param rightColumnWeight The weight of the right column. Defaults to 1f.
 * @param leftColumn The content of the left column.
 * @param middleColumn The content of the middle column.
 * @param rightColumn The content of the right column.
 */
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

/**
 * A simple layout that displays two rows. The rows are spaced by 4dp.
 *
 * @param topRow The content of the top row.
 * @param bottomRow The content of the bottom row.
 */
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
