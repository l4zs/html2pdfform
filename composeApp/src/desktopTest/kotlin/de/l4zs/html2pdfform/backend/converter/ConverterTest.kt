package de.l4zs.html2pdfform.backend.converter

import de.l4zs.html2pdfform.backend.config.Config
import de.l4zs.html2pdfform.backend.config.ConfigContext
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class ConverterTest {
    private lateinit var logger: Logger
    private lateinit var configContext: ConfigContext
    private lateinit var converter: Converter

    @BeforeTest
    fun setup() {
        logger = Logger()
        val config = Config()
        configContext = ConfigContext(config)
        converter = createConverter(logger, configContext)
        assertIs<HtmlConverter>(converter, "Converter is not an instance of HtmlConverter")
    }

    @Test
    fun `test converting html with empty form`() {
        val input = "<html><body><form></form></body></html>"
        val result = runBlocking { converter.convert(input) }
        assertIs<ByteArray>(result, "Result is not an instance of ByteArray")
    }

    @Test
    fun `test converting html without form`() {
        val input = "<html><body></body></html>"
        val result = runBlocking { converter.convert(input) }
        assertNull(result, "Result is not null")
        assert(logger.history.value.isNotEmpty()) {
            "Logger history is empty"
        }
    }
}
