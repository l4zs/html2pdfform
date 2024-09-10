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

    return calculateVisualRadiosPerRow(count, temp)
}

private fun calculateVisualRadiosPerRow(
    count: Int,
    maxRadiosPerRow: Int,
): Int {
    if (count <= maxRadiosPerRow) {
        return count
    }
    if (count % maxRadiosPerRow == 0 ||
        count % maxRadiosPerRow == maxRadiosPerRow - 1
    ) {
        return maxRadiosPerRow
    }
    val half = ceil(maxRadiosPerRow / 2.0).toInt()
    for (i in maxRadiosPerRow - 1 downTo half) {
        if (abs(count % i - i / 2) <= 1) {
            return i
        }
    }
    return maxRadiosPerRow
}

fun main() {
    for (i in 1..10) {
        for (j in (i + 1)..2 * i) {
            val perRow = calculateVisualRadiosPerRow(j, i)
            println("max $i, count $j | $perRow${j - perRow} ${(j - perRow) % perRow}")
        }
    }
}
