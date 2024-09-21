package de.l4zs.html2pdfform.backend.data

enum class Font(
    override val displayName: String,
) : Nameable {
    COURIER("Courier"),
    HELVETICA("Helvetica"),
    TIMES_ROMAN("Times-Roman"),
}
