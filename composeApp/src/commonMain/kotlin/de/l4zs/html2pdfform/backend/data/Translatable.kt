package de.l4zs.html2pdfform.backend.data

import org.jetbrains.compose.resources.StringResource

interface Translatable {
    val translationKey: String
    val resource: StringResource
}
