package de.l4zs.html2pdfform.util

import java.util.*

private const val CENTIMETERS_PER_INCH = 2.54f
private const val DOTS_PER_INCH = 72

fun Float.pointToCentimeter(): Float = this / DOTS_PER_INCH * CENTIMETERS_PER_INCH

fun Float.stringRoundedTo(decimalPlaces: Int): String =
    String.format(Locale.getDefault(Locale.Category.FORMAT), "%.${decimalPlaces}f", this)
