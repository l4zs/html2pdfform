package de.l4zs.html2pdfform.backend.util

import de.l4zs.html2pdfform.backend.config.Config

/**
 * Calculates radios per row based on the given count and the max radio
 * width.
 *
 * @param count The count of radios
 * @param maxRadioWidth The max width of a radio
 * @param config The config
 * @return The radios per row
 */
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

/**
 * Calculates the optimal radios per row based on the given count and the
 * max radios per row. This method tries to find the best fit for the
 * radios per row. For example, if the count is 1 and the maxRadiosPerRow
 * is 5, having 5 radios in the first two rows and 1 in the last row is not
 * optimal, 4 radios in the first two rows and 3 in the last row is way
 * better.
 *
 * @param count
 * @param maxRadiosPerRow
 * @return
 */
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
