package de.l4zs.html2pdfform.backend.config

import de.l4zs.html2pdfform.backend.data.Font
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An intro is a combination of an image and a text that is displayed at
 * the beginning of the PDF.
 *
 * @constructor Create empty Intro
 * @property imageEnabled If the image should be displayed
 * @property textEnabled If the text should be displayed
 * @property image The image to display
 * @property text The text to display
 */
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

/**
 * An intro image is an image displayed at the beginning of the PDF.
 *
 * @constructor Create empty Intro image
 * @property path The path to the image
 * @property width The width of the image
 */
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

/**
 * An intro text is a text displayed at the beginning of the PDF.
 *
 * @constructor Create empty Intro text
 * @property text The text to display
 * @property fontSize The font size of the text
 * @property font The font of the text
 */
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
