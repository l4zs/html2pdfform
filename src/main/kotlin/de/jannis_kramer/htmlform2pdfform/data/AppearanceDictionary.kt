package de.jannis_kramer.htmlform2pdfform.data

import de.jannis_kramer.htmlform2pdfform.Dictionary
import de.jannis_kramer.htmlform2pdfform.data.type.Name

class AppearanceDictionary : Dictionary() {

    // TODO: Change value types to the correct ones (stream or dictionary)

    fun setNormalAppearance(value: Any) {
        this[Field.NORMAL_APPEARANCE] = value
    }

    fun setRolloverAppearance(value: Any) {
        this[Field.ROLLOVER_APPEARANCE] = value
    }

    fun setDownAppearance(value: Any) {
        this[Field.DOWN_APPEARANCE] = value
    }

    enum class Field(override val key: String) : Name {
        NORMAL_APPEARANCE("N"),
        ROLLOVER_APPEARANCE("R"),
        DOWN_APPEARANCE("D"),
        ;

        override fun toString(): String {
            return "/$key"
        }
    }
}
