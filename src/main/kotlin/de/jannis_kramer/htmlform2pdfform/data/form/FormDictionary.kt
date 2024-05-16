package de.jannis_kramer.htmlform2pdfform.data.form

import de.jannis_kramer.htmlform2pdfform.Dictionary
import de.jannis_kramer.htmlform2pdfform.data.type.Name

class FormDictionary: Dictionary() {
    init {
        set(Dictionary.Field.TYPE, Dictionary.Field.Type.X_OBJECT)
        set(Dictionary.Field.SUBTYPE, Dictionary.Field.Subtype.FORM)
    }



    enum class Field(override val key: String): Name {
        FORM_TYPE("FormType"),
        BBOX("BBox"),
        MATRIX("Matrix"),
        RESOURCES("Resources"),
        GROUP("Group"),
        REFERENCE("Ref"),
        METADATA("Metadata"),
        PIECE_INFO("PieceInfo"),
        LAST_MODIFIED("LastModified"),
        STRUCT_PARENT("StructParent"),
        STRUCT_PARENTS("StructParents"),
        OPTIONAL_CONTENTS("OC"),
        FILE_SPECIFICATION("AF"),
        MEASURE("Measure"),
        POINT_DATA("PtData"),
        ;

        override fun toString(): String {
            return "/$key"
        }
    }
}
