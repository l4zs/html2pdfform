package de.janniskramer.htmlform2pdfform.data

data class Intro(
    val image: Image?,
    val text: Text?,
)

data class Image(
    val path: String,
    val width: Float,
    val height: Float,
)

data class Text(
    val text: String,
    val fontSize: Float,
)
