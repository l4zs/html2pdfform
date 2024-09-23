package de.l4zs.html2pdfform.backend.config

import de.l4zs.html2pdfform.backend.data.Font
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Intro(
    @EncodeDefault
    val imageEnabled: Boolean = true,
    @EncodeDefault
    val textEnabled: Boolean = true,
    @EncodeDefault
    val image: IntroImage? = null,
    @EncodeDefault
    val text: IntroText? = null,
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class IntroImage(
    @EncodeDefault
    val path: String = "",
    @EncodeDefault
    val width: Float = DEFAULT_WIDTH,
) {
    companion object {
        const val DEFAULT_WIDTH = 200f
    }
}

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class IntroText(
    @EncodeDefault
    val text: String = "",
    @SerialName("font_size")
    @EncodeDefault
    val fontSize: Float = DEFAULT_FONT_SIZE,
    @EncodeDefault
    val font: Font = DEFAULT_FONT,
) {
    companion object {
        const val DEFAULT_FONT_SIZE = 8f
        val DEFAULT_FONT = Font.TIMES_ROMAN
    }
}
