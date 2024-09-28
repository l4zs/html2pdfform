package de.l4zs.html2pdfform.backend.data

import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.page_size_a3
import de.l4zs.html2pdfform.resources.page_size_a4
import de.l4zs.html2pdfform.resources.page_size_a5
import org.jetbrains.compose.resources.StringResource

class PageSize(
    override val translationKey: String,
    override val resource: StringResource,
    width: Float,
    height: Float,
) : Rectangle(width, height),
    Translatable {
    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        val A3 = PageSize("page_size_a3", Res.string.page_size_a3, 842f, 1191f)
        val A4 = PageSize("page_size_a4", Res.string.page_size_a4, 595f, 842f)
        val A5 = PageSize("page_size_a5", Res.string.page_size_a5, 420f, 595f)

        val PAGE_SIZES = listOf(A3, A4, A5)

        fun of(key: String) = PAGE_SIZES.firstOrNull { it.translationKey == key }
    }
}
