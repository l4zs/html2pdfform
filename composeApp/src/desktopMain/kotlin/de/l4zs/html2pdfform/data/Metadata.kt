package de.l4zs.html2pdfform.data

import kotlinx.serialization.Serializable

@Serializable
data class Metadata(
    val author: String,
    val creator: String,
    val subject: String,
)
