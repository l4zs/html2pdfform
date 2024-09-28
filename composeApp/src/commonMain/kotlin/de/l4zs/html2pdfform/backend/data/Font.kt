package de.l4zs.html2pdfform.backend.data

import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.font_courier
import de.l4zs.html2pdfform.resources.font_helvetica
import de.l4zs.html2pdfform.resources.font_times_roman
import org.jetbrains.compose.resources.StringResource

enum class Font(
    val displayName: String,
    override val translationKey: String,
    @Transient
    override val resource: StringResource,
) : Translatable {
    COURIER("Courier", "font_courier", Res.string.font_courier),
    HELVETICA("Helvetica", "font_helvetica", Res.string.font_helvetica),
    TIMES_ROMAN("Times-Roman", "font_times_roman", Res.string.font_times_roman),
}
