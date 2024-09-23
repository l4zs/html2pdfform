package de.l4zs.html2pdfform.backend.config

import de.l4zs.html2pdfform.backend.data.Align
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

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
    val overrideNumbered: Boolean
        get() = numbered || before.isNotEmpty() && after.isNotEmpty()
}
