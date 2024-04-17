package de.jannis_kramer.htmlform2pdfform

enum class PdfName(private val key: String, val priority: Int = Int.MAX_VALUE) {
    CATALOG("Catalog"),
    FIELDS("Fields"),
    FONT("Font"),
    INFO("Info"),
    PAGE("Page"),
    PAGES("Pages"),
    UNKNOWN(""),
    XOBJECT("XObject"),
    ACROFORM("AcroForm"),
    FORM("Form"),
    LENGTH("Length"),
    FILTER("Filter"),
    CONTENTS("Contents"),
    TYPE("Type", 0),
    SUBTYPE("Subtype"),
    BASEFONT("BaseFont"),
    WIDGET("Widget"),
    RESOURCES("Resources"),
    MEDIA_BOX("MediaBox"),
    KIDS("Kids"),
    COUNT("Count"),
    PARENT("Parent"),
    ;

    override fun toString(): String {
        return "/$key"
    }
}
