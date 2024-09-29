package de.l4zs.html2pdfform.backend.data

import de.l4zs.html2pdfform.resources.Res
import de.l4zs.html2pdfform.resources.lang_english
import de.l4zs.html2pdfform.resources.lang_german
import org.jetbrains.compose.resources.StringResource
import java.util.*

/**
 * Enum class for the language of the application.
 *
 * @property translationKey The translation key for the language.
 * @property resource The resource for the language.
 */
enum class Language(
    @Transient
    override val translationKey: String,
    @Transient
    override val resource: StringResource,
) : Translatable {
    ENGLISH("lang_english", Res.string.lang_english),
    GERMAN("lang_german", Res.string.lang_german),
}

/**
 * Converts the language to a locale.
 *
 * @return The locale of the language.
 */
fun Language.toLocale(): Locale =
    when (this) {
        Language.ENGLISH -> Locale.ENGLISH
        Language.GERMAN -> Locale.GERMAN
    }
