package de.jannis_kramer.htmlform2pdfform.data.type

/**
 * Represents a text string.
 *
 * The string is encoded in UTF-8.
 *
 * [PDF 32000-2: 7.9.2.2 Text string type](https://developer.adobe.com/document-services/docs/assets/5b15559b96303194340b99820d3a70fa/PDF_ISO_32000-2.pdf#page=122)
 *
 * @param value the value of the text string.
 */
data class TextString(
    val value: String
) {
    companion object {
        /**
         * Byte order mark (BOM) for UTF-8.
         */
        const val UTF_8_BOM = "${0xEF.toChar()}${0xBB.toChar()}${0xBF.toChar()}"
    }

    override fun toString(): String {
        return "($value)"
    }
}
