package de.l4zs.html2pdfform.backend.data

import org.jetbrains.compose.resources.StringResource

/** Interface for classes that can be translated. */
interface Translatable {
    /** The translation key for this class. */
    val translationKey: String

    /** The resource that contains the translations. */
    val resource: StringResource
}
