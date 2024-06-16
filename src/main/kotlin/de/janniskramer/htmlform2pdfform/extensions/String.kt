package de.janniskramer.htmlform2pdfform.extensions

fun String.capitalize(): String = this.lowercase().replaceFirstChar { it.uppercase() }
