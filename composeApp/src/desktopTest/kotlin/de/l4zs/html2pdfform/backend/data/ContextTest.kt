package de.l4zs.html2pdfform.backend.data

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfWriter
import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.util.Logger
import org.junit.Before
import java.io.ByteArrayOutputStream
import kotlin.test.Test

fun createTestContext(): Context {
    val document = Document()
    val outputStream = ByteArrayOutputStream()
    val writer = PdfWriter.getInstance(document, outputStream)
    val logger = Logger()
    val config = Config()
    return Context(
        writer.acroForm,
        writer,
        logger,
        config,
    ).also {
        document.open()
    }
}

class ContextTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = createTestContext()
    }

    @Test
    fun `test that currentElementIndex increases by 1 for each call`() {
        val index1 = context.currentElementIndex
        val index2 = context.currentElementIndex
        assert(index1 + 1 == index2) {
            "Index should increase by 1 for each call"
        }
    }
}
