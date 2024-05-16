package de.jannis_kramer.htmlform2pdfform.data.form.button

import de.jannis_kramer.htmlform2pdfform.PDFDocument
import de.jannis_kramer.htmlform2pdfform.data.form.AnnotationDictionary
import de.jannis_kramer.htmlform2pdfform.data.type.Name

/**
 * Represents a check box field.
 * A check box is a box that can be checked or unchecked by the user.
 *
 * [PDF 32000-2: 12.7.5.2.3 Check boxes](https://developer.adobe.com/document-services/docs/assets/5b15559b96303194340b99820d3a70fa/PDF_ISO_32000-2.pdf#page=527)
 */
class CheckBoxField : ButtonField() {
    init {
        set(Field.TYPE, Field.Type.BUTTON)
        setButtonType(Type.CHECK_BOX)
    }

    override fun setValue(value: Any) {
        if (value !is State) {
            throw IllegalArgumentException("The value must be of type CheckBoxField.State.")
        }
        super.setValue(value.toString())
        set(AnnotationDictionary.Field.APPEARANCE_STATE, value.toString())
    }

    enum class State(override val key: String): Name {
        CHECKED("Yes"),
        UNCHECKED("Off"),
        ;

        override fun toString(): String {
            return "/$key"
        }
    }
}
