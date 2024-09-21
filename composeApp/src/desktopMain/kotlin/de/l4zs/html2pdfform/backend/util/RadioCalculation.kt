package de.l4zs.html2pdfform.backend.util

import de.l4zs.html2pdfform.backend.config.Config

fun calculateRadiosPerRow(
    count: Int,
    maxRadioWidth: Float,
    config: Config,
): Int {
    val maxFitPerRow =
        (config.effectivePageWidth / (maxRadioWidth + config.innerPaddingX)) // padding between radios
            .toInt()
            .coerceAtLeast(1)
            .coerceAtMost(config.maxRadiosPerRow)
            .coerceAtMost(count)

    return calculateOptimalRadiosPerRow(count, maxFitPerRow)
}

fun calculateOptimalRadiosPerRow(
    count: Int,
    maxRadiosPerRow: Int,
): Int {
    if (count <= maxRadiosPerRow) {
        return count
    }
    if (count % maxRadiosPerRow == 0 || maxRadiosPerRow < 3) {
        return maxRadiosPerRow
    }

    val maxRows = (count + maxRadiosPerRow - 1) / maxRadiosPerRow

    for (accuracy in 0..maxRadiosPerRow) {
        for (radiosPerRow in maxRadiosPerRow downTo 2) {
            val rows = (count + radiosPerRow - 1) / radiosPerRow
            if (rows <= maxRows && (count % radiosPerRow == 0 || count % radiosPerRow == maxRadiosPerRow - accuracy)) {
                return radiosPerRow
            }
        }
    }

    return maxRadiosPerRow
}
