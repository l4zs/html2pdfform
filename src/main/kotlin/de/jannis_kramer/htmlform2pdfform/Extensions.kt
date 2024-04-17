package de.jannis_kramer.htmlform2pdfform

fun Int.pad(length: Int): String {
    return this.toString().padStart(length, '0')
}

fun String.replaceLast(oldValue: String, newValue: String, ignoreCase: Boolean = false): String {
    val index = lastIndexOf(oldValue, ignoreCase = ignoreCase)
    return if (index < 0) this else this.replaceRange(index, index + oldValue.length, newValue)
}

fun String.indentLines(length: Int = 4): String {
    return this.lines().joinToString("\n" + " ".repeat(length))
}

fun String.capitalizeFirst(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}
