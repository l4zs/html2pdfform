package de.janniskramer.htmlform2pdfform

import de.janniskramer.htmlform2pdfform.config.Config
import de.janniskramer.htmlform2pdfform.converter.HtmlConverter
import java.io.File

lateinit var config: Config
    private set

fun main() {
    val input = File("files/form.html")
    val output = File("files/form.pdf")

    config = Config()

    val converter = HtmlConverter()

    converter.parse(input, output)
}
