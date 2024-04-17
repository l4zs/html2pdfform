package de.jannis_kramer.htmlform2pdfform

class PdfStream(
    private val dictionary: Dictionary,
    private var content: String = "",
) {

    fun setContent(content: String) {
        this.content = content
    }

    /**
     * Returns a string representation of the object.
     *
     * For example:
     * ```
     *     << /key value
     *         /key value
     *         ...
     *         /Length <content-length>
     *     >>
     * stream
     * <content>
     * endstream
     * ```
     *
     * @return a string representation of the object.
     */
    override fun toString(): String {
        dictionary[PdfName.LENGTH] = content.length
        return """
        |    ${dictionary.toString().indentLines()}
        |stream
        |$content
        |endstream
    """.trimMargin()
    }

    operator fun set(s: PdfName, value: Any) {
        dictionary[s] = value
    }
}
