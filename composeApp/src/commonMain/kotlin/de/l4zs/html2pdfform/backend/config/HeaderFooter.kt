package de.l4zs.html2pdfform.backend.config

import de.l4zs.html2pdfform.backend.data.Align
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Header and footer are used to add content to the top and bottom of the
 * page. The content can contain text before and after the page number, be
 * numbered and aligned. When there is content before and after the page
 * number, the page number is always displayed.
 *
 * @constructor Create empty Header footer
 * @property before The content before the page number
 * @property after The content after the page number
 * @property numbered If the page number should be displayed
 * @property align The alignment of the content
 */
@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class HeaderFooter(
    @EncodeDefault
    val before: String = "",
    @EncodeDefault
    val after: String = "",
    @EncodeDefault
    val numbered: Boolean = false,
    @EncodeDefault
    val align: Align = Align.RIGHT,
) {
    /**
     * This property is used to determine if the page number should be
     * displayed. The page number is always displayed when there is content
     * before and after the page number.
     */
    @Transient
    val overrideNumbered = numbered || before.isNotEmpty() && after.isNotEmpty()
}
