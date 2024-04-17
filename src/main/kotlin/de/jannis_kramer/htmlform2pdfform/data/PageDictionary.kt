package de.jannis_kramer.htmlform2pdfform.data

import de.jannis_kramer.htmlform2pdfform.Dictionary
import de.jannis_kramer.htmlform2pdfform.data.type.Name

class PageDictionary: Dictionary() {

    enum class Field(override val key: String): Name {
        RESOURCES("Resources"),
        CONTENTS("Contents"),
        PARENT("Parent"),
        ANNOTS("Annots"),
        ;

        override fun toString(): String {
            return "/$key"
        }
    }
}
