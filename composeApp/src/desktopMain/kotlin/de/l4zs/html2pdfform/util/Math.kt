package de.l4zs.html2pdfform.util

fun Float.pointToCentimeter(): Float = this / 72 * 2.54f

fun Float.centimeterToPoint(): Float = this * 72 / 2.54f
