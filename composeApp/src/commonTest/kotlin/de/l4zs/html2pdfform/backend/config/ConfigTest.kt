package de.l4zs.html2pdfform.backend.config

import kotlinx.serialization.SerializationException
import kotlin.test.*

class ConfigTest {
    private lateinit var config: Config

    @BeforeTest
    fun setup() {
        config = Config()
    }

    @Test
    fun `test config (de-)serializing`() {
        val serialized = assertIs<String>(exportConfig(config), "Serialization failed")
        require(serialized.isNotBlank()) { "Serialized Config is blank" }
        val deserialized = assertIs<Config>(importConfig(serialized), "Deserialization failed")
        assertEquals(config, deserialized, "Deserialized Config does not match original")
    }

    @Test
    fun `test default config equals`() {
        assertEquals(config, Config(), "Default Config does not match original")
    }

    @Test
    fun `test deserialization with faulty config`() {
        val serialized = "faulty config"
        assertFailsWith<SerializationException>("Deserialization should fail") { importConfig(serialized) }
    }
}
