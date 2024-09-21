package de.l4zs.html2pdfform.backend.config

import de.l4zs.html2pdfform.backend.data.Align
import kotlinx.serialization.Serializable

@Serializable
data class HeaderFooter(
    val before: String = "",
    val after: String = "",
    val numbered: Boolean = false,
    val align: Align = Align.RIGHT,
) {
    val overrideNumbered: Boolean
        get() = numbered || before.isNotEmpty() && after.isNotEmpty()
}
