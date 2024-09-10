package de.l4zs.html2pdfform.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Intro(
    val image: Image?,
    val text: Text?,
)

@Serializable
data class Image(
    val path: String,
    val width: Float,
    val height: Float,
)

@Serializable
data class Text(
    val text: String,
    @SerialName("font_size")
    val fontSize: Float,
    val font: String,
)
