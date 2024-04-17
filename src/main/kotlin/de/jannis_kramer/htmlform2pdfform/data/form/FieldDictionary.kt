package de.jannis_kramer.htmlform2pdfform.data.form

import de.jannis_kramer.htmlform2pdfform.Dictionary
import de.jannis_kramer.htmlform2pdfform.data.type.Name
import de.jannis_kramer.htmlform2pdfform.PdfObject
import de.jannis_kramer.htmlform2pdfform.data.DictionaryObject
import de.jannis_kramer.htmlform2pdfform.data.type.TextString

/**
 * Represents a dictionary for form fields.
 *
 * [PDF 32000-2: 12.7.4 Field dictionaries](https://developer.adobe.com/document-services/docs/assets/5b15559b96303194340b99820d3a70fa/PDF_ISO_32000-2.pdf#page=521)
 */
open class FieldDictionary : AnnotationDictionary() {

    init {
        set(Dictionary.Field.SUBTYPE, AnnotationDictionary.Field.Subtype.WIDGET)
    }

    fun setType(type: Field.Type) {
        this[Field.TYPE] = type
    }

    fun setParent(parent: DictionaryObject<*>) {
        this[Field.PARENT] = parent.getReference()
    }

    fun setKids(kids: Collection<PdfObject>) {
        this[Field.KIDS] = kids.map { it.getReference() }
    }

    fun setName(name: TextString) {
        assert(!name.value.contains(".")) { "Name must not contain a period ('.')" }
        this[Field.NAME] = name
    }

    fun setAlternativeName(alternativeName: TextString) {
        this[Field.ALTERNATIVE_NAME] = alternativeName
    }

    fun setMappingName(mappingName: TextString) {
        this[Field.MAPPING_NAME] = mappingName
    }

    fun setFlag(vararg flags: Field.Flag) {
        setFlag(*flags.map { it.value }.toIntArray())
    }

    fun setFlag(vararg values: Int) {
        this[Field.FLAGS] = ((this[Field.FLAGS] as? Int) ?: 0) or values.sum()
    }

    fun unsetFlag(vararg flags: Field.Flag) {
        unsetFlag(*flags.map { it.value }.toIntArray())
    }

    fun unsetFlag(vararg values: Int) {
        this[Field.FLAGS] = ((this[Field.FLAGS] as? Int) ?: 0) and values.sum().inv()
    }

    fun toggleFlag(vararg flags: Field.Flag) {
        toggleFlag(*flags.map { it.value }.toIntArray())
    }

    fun toggleFlag(vararg value: Int) {
        this[Field.FLAGS] = ((this[Field.FLAGS] as? Int) ?: 0) xor value.sum()
    }

    open fun setValue(value: Any) {
        this[Field.VALUE] = value
    }

    open fun setDefaultValue(defaultValue: Any) {
        this[Field.DEFAULT_VALUE] = defaultValue
    }

    fun setAdditionalActions(additionalActions: DictionaryObject<*>) {
        this[Field.ADDITIONAL_ACTIONS] = additionalActions.getReference()
    }

    /**
     * Fields.
     *
     * PDF 32000-2: Table 226 – Entries common to all field dictionaries
     */
    enum class Field(override val key: String) : Name {
        TYPE("FT"),
        PARENT("Parent"),
        KIDS("Kids"),
        NAME("T"),
        ALTERNATIVE_NAME("TU"),
        MAPPING_NAME("TM"),
        FLAGS("Ff"),
        VALUE("V"),
        DEFAULT_VALUE("DV"),
        ADDITIONAL_ACTIONS("AA"),
        ;

        override fun toString(): String {
            return "/$key"
        }

        /**
         * Field types.
         *
         * PDF 32000-2: Table 226 – Entries common to all field dictionaries (used in the FT entry)
         */
        enum class Type(private val value: String) {
            BUTTON("Btn"),
            TEXT("Tx"),
            CHOICE("Ch"),
            SIGNATURE("Sig"),
            ;

            override fun toString(): String {
                return "/$value"
            }
        }

        /**
         * Field flags.
         *
         * PDF 32000-2: Table 227 – Field flags common to all field types
         */
        enum class Flag(val value: Int) {
            READ_ONLY(1 shl 0),
            REQUIRED(1 shl 1),
            NO_EXPORT(1 shl 2),
        }
    }
}
