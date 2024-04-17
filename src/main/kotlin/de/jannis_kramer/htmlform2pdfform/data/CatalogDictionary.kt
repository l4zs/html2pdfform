package de.jannis_kramer.htmlform2pdfform.data

import de.jannis_kramer.htmlform2pdfform.Dictionary
import de.jannis_kramer.htmlform2pdfform.data.type.Name

class CatalogDictionary: Dictionary() {


    enum class Field(override val key: String): Name {
        VERSION("Version"),
        EXTENSIONS("Extensions"),
        PAGES("Pages"),
        PAGE_LABELS("PageLabels"),
        NAMES("Names"),
        DESTS("Dests"),
        VIEWER_PREFERENCES("ViewerPreferences"),
        PAGE_LAYOUT("PageLayout"),
        PAGE_MODE("PageMode"),
        OUTLINES("Outlines"),
        THREADS("Threads"),
        OPEN_ACTION("OpenAction"),
        ADDITIONAL_ACTIONS("AA"),
        URI("URI"),
        ACRO_FORM("AcroForm"),
        METADATA("Metadata"),
        STRUCT_TREE_ROOT("StructTreeRoot"),
        MARK_INFO("MarkInfo"),
        LANGUAGE("Lang"),
        SPIDER_INFO("SpiderInfo"),
        OUTPUT_INTENTS("OutputIntents"),
        PIECE_INFO("PieceInfo"),
        OC_PROPERTIES("OCProperties"),
        PERMS("Perms"),
        LEGAL("Legal"),
        REQUIREMENTS("Requirements"),
        COLLECTION("Collection"),
        DSS("DSS"),
        ASSOCIATED_FILES("AF"),
        DOCUMENT_PARTS_ROOT("DPartRoot"),
        ;

        override fun toString(): String {
            return "/$key"
        }
    }
}
