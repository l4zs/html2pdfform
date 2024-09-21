package de.l4zs.html2pdfform.backend.data

enum class Align(
    val align: Int,
    override val displayName: String,
) : Nameable {
    LEFT(0, "Links"),
    CENTER(1, "Zentriert"),
    RIGHT(2, "Rechts"),
}
