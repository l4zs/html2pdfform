package de.jannis_kramer.htmlform2pdfform

enum class PDFVersion {
    V1_0,
    V1_1,
    V1_2,
    V1_3,
    V1_4,
    V1_5,
    V1_6,
    V1_7,
    V2_0,
    ;

    override fun toString(): String {
        // e.g. "PDF-2.0"
        return "PDF-${name.substring(1).replace("_", ".")}"
    }
}
