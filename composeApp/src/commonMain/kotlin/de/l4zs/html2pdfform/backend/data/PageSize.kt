package de.l4zs.html2pdfform.backend.data

class PageSize(
    override val displayName: String,
    width: Float,
    height: Float,
) : Rectangle(width, height),
    Nameable {
    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        val A3 = PageSize("A3", 842f, 1191f)
        val A4 = PageSize("A4", 595f, 842f)
        val A5 = PageSize("A5", 420f, 595f)

        val PAGE_SIZES = listOf(A3, A4, A5)

        fun of(name: String) = PAGE_SIZES.firstOrNull { it.displayName == name }
    }
}
