package de.jannis_kramer.htmlform2pdfform.data.form.button

/**
 * Represents a push button field.
 * A push button is a button that can be clicked by the user.
 *
 * This field does not retain permanent value, so it shall not use the [FieldDictionary.Field.VALUE] and [FieldDictionary.Field.DEFAULT_VALUE] entries.
 *
 * [PDF 32000-2: 12.7.5.2.2 Push-buttons](https://developer.adobe.com/document-services/docs/assets/5b15559b96303194340b99820d3a70fa/PDF_ISO_32000-2.pdf#page=526)
 */
class PushButtonField: ButtonField() {
    init {
        this[Field.TYPE] = Field.Type.BUTTON
        setButtonType(Type.PUSH_BUTTON)
    }
}
