package de.janniskramer.htmlform2pdfform

import de.janniskramer.htmlform2pdfform.converter.HtmlConverter
import java.io.File

fun main() {
    val input = File("files/form.html")
    val output = File("files/form.pdf")

    val converter = HtmlConverter()

    converter.parse(input, output)
}
