package de.l4zs.html2pdfform.util

import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LoggerTest {
    private lateinit var logger: Logger

    @Before
    fun setUp() {
        logger = Logger()
    }

    @Test
    fun `test logging`() {
        logger.error("Error")
        assertEquals(1, logger.history.value.size, "One log entry should be present")
        assertEquals(Logger.LogLevel.ERROR, logger.history.value[0].level, "Log level should be ERROR")
        assertEquals("Error", logger.history.value[0].message, "Message should be 'Error'")
        assertNull(logger.history.value[0].error, "Error should be null")
    }

    @Test
    fun `test log level`() {
        logger.logLevel = Logger.LogLevel.INFO
        logger.debug("Debug")
        assertEquals(0, logger.history.value.size, "No log entry should be present")
    }
}
