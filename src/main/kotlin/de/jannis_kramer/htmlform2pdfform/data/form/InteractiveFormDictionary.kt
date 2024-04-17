package de.jannis_kramer.htmlform2pdfform.data.form

import de.jannis_kramer.htmlform2pdfform.Dictionary
import de.jannis_kramer.htmlform2pdfform.data.type.Name
import de.jannis_kramer.htmlform2pdfform.PdfObject

/**
 * Represents an interactive form dictionary.
 *
 * [PDF 32000-2: 12.7.3 Interactive Form Dictionary](https://developer.adobe.com/document-services/docs/assets/5b15559b96303194340b99820d3a70fa/PDF_ISO_32000-2.pdf#page=519)
 */
class InteractiveFormDictionary(): Dictionary() {

    fun setFields(fields: Collection<PdfObject>) {
        this[Field.FIELDS] = fields.map { it.getReference() }
    }

    fun setSigFlags(sigFlags: Int) {
        this[Field.SIG_FLAGS] = sigFlags
    }

    fun setCO(co: Collection<PdfObject>) {
        this[Field.CO] = co.map { it.getReference() }
    }

    fun setDR(dr: PdfObject) {
        this[Field.DR] = dr.getReference()
    }

    fun setDA(da: String) {
        this[Field.DA] = da
    }

    fun setQ(q: Int) {
        this[Field.Q] = q
    }

    enum class Field(override val key: String): Name {
        FIELDS("Fields"),
        SIG_FLAGS("SigFlags"),
        CO("CO"),
        DR("DR"),
        DA("DA"),
        Q("Q"),
        ;

        override fun toString(): String {
            return "/$key"
        }
    }
}
