package de.l4zs.html2pdfform.util

import de.l4zs.html2pdfform.config.config
import de.l4zs.html2pdfform.config.effectivePageWidth
import kotlin.math.abs
import kotlin.math.ceil

private const val CENTIMETERS_PER_INCH = 2.54f
private const val DOTS_PER_INCH = 72

fun Float.pointToCentimeter(): Float = this / DOTS_PER_INCH * CENTIMETERS_PER_INCH

fun Float.centimeterToPoint(): Float = this * DOTS_PER_INCH / CENTIMETERS_PER_INCH

fun calculateRadiosPerRow(
    count: Int,
    maxRadioWidth: Float,
): Int {
    val temp =
        (config.effectivePageWidth / (maxRadioWidth + config.innerPaddingX)) // padding between radios
            .toInt()
            .coerceAtLeast(1)
            .coerceAtMost(config.maxRadiosPerRow)
            .coerceAtMost(count)

    return calculateOptimalRadiosPerRow(count, temp)
}

fun calculateOptimalRadiosPerRow(count: Int, maxRadiosPerRow: Int): Int {
    if (count <= maxRadiosPerRow) {
        return count
    }
    if (count % maxRadiosPerRow == 0 || maxRadiosPerRow < 3) {
        return maxRadiosPerRow
    }

    val maxRows = (count + maxRadiosPerRow - 1) / maxRadiosPerRow

    (0..maxRadiosPerRow).forEach { i ->
        (maxRadiosPerRow downTo 2).forEach {
            val rows = (count + it - 1) / it
            if(rows <= maxRows && (count % it == 0 || count % it == maxRadiosPerRow - i)) {
                return it
            }
        }
    }

    return maxRadiosPerRow
}
