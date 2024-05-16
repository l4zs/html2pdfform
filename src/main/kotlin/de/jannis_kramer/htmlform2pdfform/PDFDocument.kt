package de.jannis_kramer.htmlform2pdfform

import de.jannis_kramer.htmlform2pdfform.convert.pad
import java.io.File

class PDFDocument(
    private val version: PDFVersion = PDFVersion.V2_0,
) {

    private val file: File = File("files/document.pdf").also {
        it.parentFile.mkdirs()
        it.createNewFile()
    }
    private val out = StringBuilder()
    private val objects = mutableListOf<PdfObject>()
    var nextId: Int = 1
    private var baseOffset = 0
    private var xrefOffset = 0

    fun add(obj: PdfObject) {
        objects.add(obj)
    }

    fun build() {
        objects.sortBy { it.id }
        writeHeader()
        writeBody()
        writeCrossReferenceTable()
        writeTrailer()

        file.writer()
            .append(out)
            .close()
    }

    private fun writeHeader() {
        baseOffset = out.length
        out.appendLine("%$version")
        // if the pdf contains binary data, we need to add a comment with 4 high-bit bytes (code >=128)
        out.appendLine("%${0x80.toChar()}${0x81.toChar()}${0x82.toChar()}${0x83.toChar()}")
        out.appendLine()
    }

    private fun writeBody() {
        // loop through objects and write them
        objects.forEach {
            it.offset = out.length - baseOffset
            out.appendLine(it.build())
            out.appendLine()
        }
    }

    private fun writeCrossReferenceTable() {
        // generate cross reference entries for each indirect object
        // each line has to be EXACTLY 20 bytes long
        // <10-bit byte offset with leading zeros> SP <5-bit generation number with leading zeros> SP <'n' used or 'f' free> SP LF
        xrefOffset = out.length - baseOffset
        out.appendLine("xref")
        out.appendLine("0 ${objects.size + 1}")
        out.appendLine("0000000000 65535 f ")
        objects.forEach {
            out.appendLine("${it.offset.pad(10)} ${it.generation.pad(5)} n ")
        }
    }

    private fun writeTrailer() {
        out.append(
            """           
            trailer
                << /Root ${objects[0].getReference()}
                    /Size ${objects.size + 1}
                >>
            startxref
            $xrefOffset
            %%EOF
            """.trimIndent()
        )
    }
}

fun pdf(version: PDFVersion = PDFVersion.V2_0, block: PDFDocument.() -> Unit = {}): PDFDocument {
    return PDFDocument(version).apply(block)
}
