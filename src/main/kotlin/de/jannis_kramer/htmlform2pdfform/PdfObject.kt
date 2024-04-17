package de.jannis_kramer.htmlform2pdfform

abstract class PdfObject {
    abstract val id: Int
    val generation: Int = 0
    var offset: Int = 0

    fun getReference(): String {
        return "$id $generation R"
    }

    abstract override fun toString(): String
    fun build(): String {
        return """
            |$id $generation obj
            |$this
            |endobj
        """.trimMargin()
    }
}
