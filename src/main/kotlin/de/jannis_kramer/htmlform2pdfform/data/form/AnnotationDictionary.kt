package de.jannis_kramer.htmlform2pdfform.data.form

import de.jannis_kramer.htmlform2pdfform.Dictionary
import de.jannis_kramer.htmlform2pdfform.data.type.Name
import de.jannis_kramer.htmlform2pdfform.data.type.Rectangle

/**
 * Represents an annotation dictionary.
 *
 * [PDF 32000-2: 12.5.2 Annotation dictionaries](https://developer.adobe.com/document-services/docs/assets/5b15559b96303194340b99820d3a70fa/PDF_ISO_32000-2.pdf#page=462)
 */
open class AnnotationDictionary : Dictionary() {

    init {
        set(Dictionary.Field.TYPE, Dictionary.Field.Type.ANNOT)
    }

    fun setRect(rect: Rectangle) {
        this[Field.RECT] = rect
    }

    // TODO: other functions

    enum class Field(override val key: String): Name {
        RECT("Rect"),
        CONTENTS("Contents"),
        PAGE("P"),
        NAME("NM"),
        MODIFIED("M"),
        FLAGS("F"),
        APPEARANCE_DICTIONARY("AP"),
        APPEARANCE_STATE("AS"),
        BORDER("Border"),
        COLOR("C"),
        STRUCT_PARENT("StructParent"),
        OPTIONAL_CONTENT("OC"),
        ASSOCIATED_FILES("AF"),
        ca("ca"),
        CA("CA"),
        BLEND_MODE("BM"),
        LANGUAGE("Lang"),

        ;

        override fun toString(): String {
            return "/$key"
        }

        enum class Subtype(override val key: String): Name {
            TEXT("Text"),
            LINK("Link"),
            FREE_TEXT("FreeText"),
            LINE("Line"),
            SQUARE("Square"),
            CIRCLE("Circle"),
            POLYGON("Polygon"),
            POLYLINE("PolyLine"),
            HIGHLIGHT("Highlight"),
            UNDERLINE("Underline"),
            SQUIGGLY("Squiggly"),
            STRIKE_OUT("StrikeOut"),
            CARET("Caret"),
            STAMP("Stamp"),
            INK("Ink"),
            POPUP("Popup"),
            FILE_ATTACHMENT("FileAttachment"),
            SCREEN("Screen"),
            WIDGET("Widget"),
            PRINTER_MARK("PrinterMark"),
            TRAP_NET("TrapNet"),
            WATERMARK("Watermark"),
            THREE_DIMENSIONAL("3D"),
            REDACT("Redact"),
            PROJECTION("Projection"),
            RICH_MEDIA("RichMedia"),
            ;

            override fun toString(): String {
                return "/$key"
            }
        }
    }
}
