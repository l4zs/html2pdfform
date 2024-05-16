package de.jannis_kramer.htmlform2pdfform

import de.jannis_kramer.htmlform2pdfform.convert.indentLines
import de.jannis_kramer.htmlform2pdfform.data.type.Name


open class Dictionary {
    private val entries = mutableMapOf<String, Any>()

    /**
     * Returns a string representation of the object.
     *
     * For example:
     * ```
     * << /key value
     *     /key value
     *     ...
     * >>
     * ```
     *
     * @return a string representation of the object.
     */
    override fun toString(): String {
        return """
        |<< ${entries.format().indentLines()}
        |>>
    """.trimMargin()
    }

    private fun MutableMap<String, Any>.format(): String {
        return this.map { it }
            .filterNot { it.value == PdfName.UNKNOWN }
//            .sortedBy { attributePriority[it.key] ?: 0 }
            .joinToString("\n") {
                if (it.value is Dictionary) {
                    return@joinToString "${it.key} ${it.value.toString().replaceFirst("<< ", "\n<< ")}"
                }
                "${it.key} ${it.value}"
            }
    }

    operator fun get(s: String): Any? {
        return entries[s]
    }

    operator fun get(s: Name): Any? {
        return entries[s.toString()]
    }

    operator fun set(s: String, value: Any) {
        entries["/$s"] = value
    }

    operator fun set(s: PdfName, value: Any) {
        entries[s.toString()] = value
    }

    operator fun set(s: Name, value: Any) {
        entries[s.toString()] = value
    }

    enum class Field(override val key: String): Name {
        TYPE("Type"),
        SUBTYPE("Subtype"),
        BBOX("BBox"),
        ;

        override fun toString(): String {
            return "/$key"
        }

        enum class Type(override val key: String): Name {
            ANNOT("Annot"),
            CATALOG("Catalog"),
            X_OBJECT("XObject"),
            PAGES("Pages"),
            ;

            override fun toString(): String {
                return "/$key"
            }
        }

        enum class Subtype(override val key: String): Name {
            FORM("Form"),
            ;

            override fun toString(): String {
                return "/$key"
            }
        }
    }
}

private val attributePriority = PdfName.entries.toTypedArray().associate { it.toString() to it.priority }

fun dictionary(block: Dictionary.() -> Unit = {}): Dictionary {
    return Dictionary().apply(block)
}

fun <T : Dictionary> dictionary(factory: () -> T, block: T.() -> Unit = {}): T {
    return factory().apply(block)
}
