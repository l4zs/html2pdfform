package de.l4zs.html2pdfform.backend.config

import de.l4zs.html2pdfform.backend.data.Font
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Intro(
    val imageEnabled: Boolean,
    val textEnabled: Boolean,
    val image: IntroImage?,
    val text: IntroText?,
)

@Serializable
data class IntroImage(
    val path: String,
    val width: Float = DEFAULT_WIDTH,
) {
    companion object {
        const val DEFAULT_WIDTH = 200f
    }
}

@Serializable
data class IntroText(
    val text: String,
    @SerialName("font_size")
    val fontSize: Float = DEFAULT_FONT_SIZE,
    val font: Font = DEFAULT_FONT,
) {
    companion object {
        const val DEFAULT_FONT_SIZE = 8f
        val DEFAULT_FONT = Font.TIMES_ROMAN
    }
}
