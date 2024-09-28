package de.l4zs.html2pdfform.backend.config

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import de.l4zs.html2pdfform.util.Logger
import kotlinx.coroutines.runBlocking
import org.junit.Before
import java.io.File
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DesktopConfigTest {
    private lateinit var config: Config
    private lateinit var logger: Logger
    private lateinit var testFile: File

    @Before
    fun setUp() {
        config = Config()
        logger = Logger()
        testFile = File.createTempFile("config", ".json")
        testFile.deleteOnExit()
        assertTrue(testFile.readText().isBlank(), "initial file is not empty")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `test saving config to file`() =
        runComposeUiTest {
            runBlocking {
                saveConfigToFile(config, logger, testFile)
            }
            assertTrue(testFile.readText().isNotBlank(), "File is empty")
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `test loading config from file`() =
        runComposeUiTest {
            runBlocking {
                saveConfigToFile(config, logger, testFile)
            }
            assertTrue(testFile.readText().isNotBlank(), "File is empty")
            assertIs<Config>(
                runBlocking {
                    loadConfigFromFile(logger, testFile)
                },
                "Returned object is not of type Config",
            )
        }
}
