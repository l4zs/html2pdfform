package de.jannis_kramer.htmlform2pdfform.data

import de.jannis_kramer.htmlform2pdfform.Dictionary
import de.jannis_kramer.htmlform2pdfform.PDFDocument
import de.jannis_kramer.htmlform2pdfform.PdfObject
import de.jannis_kramer.htmlform2pdfform.data.type.Name
import de.jannis_kramer.htmlform2pdfform.convert.indentLines

open class DictionaryObject<T : Dictionary>(override val id: Int, val dictionary: T) : PdfObject() {

    override fun toString(): String {
        return """
            |    ${dictionary.toString().indentLines()}
        """.trimMargin()
    }

    operator fun set(key: Name, value: Any) {
        dictionary[key] = value
    }
}

fun PDFDocument.dictionaryObject(
    type: Name? = null,
    block: DictionaryObject<Dictionary>.() -> Unit = {},
): DictionaryObject<Dictionary> {
    return dictionaryObject(::Dictionary, type, block)
}

fun <T : Dictionary> PDFDocument.dictionaryObject(
    factory: () -> T,
    type: Name? = null,
    block: DictionaryObject<T>.() -> Unit = {},
): DictionaryObject<T> {
    val obj = DictionaryObject(nextId++, factory()).apply(block)
    if (type != null) {
        obj[Dictionary.Field.TYPE] = type
    }
    add(obj)
    return obj
}
