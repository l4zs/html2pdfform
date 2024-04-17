package de.jannis_kramer.htmlform2pdfform.data.form.button

import de.jannis_kramer.htmlform2pdfform.data.form.FieldDictionary

/**
 * Represents a button field.
 * A button field is a field that can be clicked by the user.
 * It can be a [PushButtonField], a check box or a radio button.
 *
 * [PDF 32000-2: 12.7.5.2 Button fields](https://developer.adobe.com/document-services/docs/assets/5b15559b96303194340b99820d3a70fa/PDF_ISO_32000-2.pdf#page=526)
 */
open class ButtonField : FieldDictionary() {

    fun setButtonType(type: Type) {
        when (type) {
            Type.PUSH_BUTTON -> {
                unsetFlag(Flags.NO_TOGGLE_TO_OFF, Flags.RADIO, Flags.RADIO_IN_UNISON)
                setFlag(Flags.PUSHBUTTON)
            }

            Type.CHECK_BOX -> {
                unsetFlag(
                    Flags.NO_TOGGLE_TO_OFF,
                    Flags.RADIO,
                    Flags.PUSHBUTTON,
                    Flags.RADIO_IN_UNISON
                )
            }

            Type.RADIO_BUTTON -> {
                unsetFlag(Flags.PUSHBUTTON)
                setFlag(Flags.RADIO)
            }
        }
    }

    fun setFlag(vararg flag: Flags) {
        setFlag(*flag.map { it.value }.toIntArray())
    }

    fun unsetFlag(vararg flag: Flags) {
        unsetFlag(*flag.map { it.value }.toIntArray())
    }

    fun toggleFlag(vararg flag: Flags) {
        toggleFlag(*flag.map { it.value }.toIntArray())
    }

    enum class Flags(val value: Int) {
        NO_TOGGLE_TO_OFF(1 shl 14), // radio only
        RADIO(1 shl 15), // set if radio button, otherwise check box (or push button, when push button flag is set)
        PUSHBUTTON(1 shl 16), // set if push button, otherwise check box or radio button
        RADIO_IN_UNISON(1 shl 25), // radio only
    }

    enum class Type {
        PUSH_BUTTON,
        CHECK_BOX,
        RADIO_BUTTON,
    }
}
