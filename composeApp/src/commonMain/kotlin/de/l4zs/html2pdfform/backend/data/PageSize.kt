package de.l4zs.html2pdfform.backend.data

import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.page_size_a3
import de.l4zs.html2pdfform.resources.page_size_a4
import de.l4zs.html2pdfform.resources.page_size_a5
import org.jetbrains.compose.resources.StringResource

/**
 * Represents a page size with a width and height.
 *
 * @param width The width of the page.
 * @param height The width and height of the page.
 * @constructor Creates a new page size with the given width and height.
 * @property translationKey The translation key for the page size.
 * @property resource The resource for the page size.
 */
class PageSize(
    width: Float,
    height: Float,
    @Transient
    override val translationKey: String,
    @Transient
    override val resource: StringResource,
) : Rectangle(width, height),
    Translatable {
    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        /** The A3 page size. */
        val A3 = PageSize(842f, 1191f, "page_size_a3", Res.string.page_size_a3)

        /** The A4 page size. */
        val A4 = PageSize(595f, 842f, "page_size_a4", Res.string.page_size_a4)

        /** The A5 page size. */
        val A5 = PageSize(420f, 595f, "page_size_a5", Res.string.page_size_a5)

        /** All page sizes. */
        val PAGE_SIZES = listOf(A3, A4, A5)

        /**
         * Gets the page size with the given translation key.
         *
         * @param key The translation key of the page size.
         * @return The page size with the given translation key.
         */
        fun of(key: String) = PAGE_SIZES.firstOrNull { it.translationKey == key }
    }
}
