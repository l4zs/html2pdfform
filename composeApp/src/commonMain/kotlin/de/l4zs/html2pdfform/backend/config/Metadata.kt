package de.l4zs.html2pdfform.backend.config

import kotlinx.serialization.Serializable

/**
 * Metadata for the PDF file.
 *
 * @param author The author of the PDF file.
 * @param creator The creator of the PDF file.
 * @param subject The subject of the PDF file.
 */
@Serializable
data class Metadata(
    val author: String,
    val creator: String,
    val subject: String,
)
