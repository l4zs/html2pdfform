package de.l4zs.html2pdfform.util

import java.util.*

private const val CENTIMETERS_PER_INCH = 2.54f
private const val DOTS_PER_INCH = 72

/**
 * Converts a float value from points to inches.
 */
fun Float.pointToInch(): Float = this / DOTS_PER_INCH

/**
 * Converts a float value from inches to centimeters.
 */
fun Float.inchToCentimeter(): Float = this * CENTIMETERS_PER_INCH

/**
 * Converts a float value from points to centimeters.
 */
fun Float.pointToCentimeter(): Float = pointToInch().inchToCentimeter()

/**
 * Rounds a float value to a given number of decimal places and returns it as a string.
 */
fun Float.stringRoundedTo(decimalPlaces: Int): String =
    String.format(Locale.getDefault(Locale.Category.FORMAT), "%.${decimalPlaces}f", this)
