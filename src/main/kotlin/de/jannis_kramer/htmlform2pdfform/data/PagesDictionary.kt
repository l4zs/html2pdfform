package de.jannis_kramer.htmlform2pdfform.data

import de.jannis_kramer.htmlform2pdfform.Dictionary
import de.jannis_kramer.htmlform2pdfform.data.type.Name

class PagesDictionary : Dictionary() {

    enum class Field(override val key: String) : Name {
        MEDIA_BOX("MediaBox"),
        KIDS("Kids"),
        COUNT("Count"),
        ;

        override fun toString(): String {
            return "/$key"
        }
    }
}
