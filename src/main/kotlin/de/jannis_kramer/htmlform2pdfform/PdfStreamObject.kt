package de.jannis_kramer.htmlform2pdfform

import de.jannis_kramer.htmlform2pdfform.data.DictionaryObject

class PdfStreamObject(
    override var id: Int,
) : DictionaryObject<Dictionary>(id, Dictionary()) {
    private val stream: PdfStream = PdfStream(dictionary)

    fun setContent(content: String) {
        stream.setContent(content)
    }

    override fun toString(): String {
        return stream.toString()
    }
}

fun PDFDocument.streamObject(
    block: PdfStreamObject.() -> Unit = {},
): PdfStreamObject {
    val obj = PdfStreamObject(nextId++).apply(block)
    add(obj)
    return obj
}
