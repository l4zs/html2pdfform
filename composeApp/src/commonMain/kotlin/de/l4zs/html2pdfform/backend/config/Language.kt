package de.l4zs.html2pdfform.backend.config

import de.l4zs.html2pdfform.backend.data.Translatable
import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.lang_english
import de.l4zs.html2pdfform.resources.lang_german
import org.jetbrains.compose.resources.StringResource
import java.util.*

enum class Language(
    override val translationKey: String,
    @Transient
    override val resource: StringResource,
) : Translatable {
    ENGLISH("lang_english", Res.string.lang_english),
    GERMAN("lang_german", Res.string.lang_german),
}

fun Language.toLocale(): Locale = when (this) {
    Language.ENGLISH -> Locale.ENGLISH
    Language.GERMAN -> Locale.GERMAN
}
