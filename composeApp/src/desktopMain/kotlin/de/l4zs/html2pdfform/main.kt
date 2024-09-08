package de.l4zs.html2pdfform

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.l4zs.html2pdfform.ui.PDFFormGeneratorApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "html2pdfform",
    ) {
        PDFFormGeneratorApp()
    }
}

//lateinit var config: Config
//    private set
//
//fun test() {
//    val input = File("files/form.html")
//    val output = File("files/form.pdf")
//
//    config = Config()
//
//    val converter = HtmlConverter()
//
//    converter.convert(input, output)
//}
