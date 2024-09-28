package de.l4zs.html2pdfform.backend.data

import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.align_center
import de.l4zs.html2pdfform.resources.align_left
import de.l4zs.html2pdfform.resources.align_right
import org.jetbrains.compose.resources.StringResource

enum class Align(
    val align: Int,
    override val translationKey: String,
    @Transient
    override val resource: StringResource,
) : Translatable {
    LEFT(0, "align_left", Res.string.align_left),
    CENTER(1, "align_center", Res.string.align_center),
    RIGHT(2, "align_right", Res.string.align_right),
}
