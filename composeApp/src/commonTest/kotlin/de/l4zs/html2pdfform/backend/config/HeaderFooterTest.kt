package de.l4zs.html2pdfform.backend.config

import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HeaderFooterTest {
    private lateinit var headerFooter: HeaderFooter

    @BeforeTest
    fun setup() {
        headerFooter = HeaderFooter()
    }

    @Test
    fun `test headerfooter (de-)serializing`() {
        val json = Json
        val serialized =
            assertIs<String>(json.encodeToString(HeaderFooter.serializer(), headerFooter), "Serialization failed")
        require(serialized.isNotBlank()) { "Serialized HeaderFooter is blank" }
        val deserialized =
            assertIs<HeaderFooter>(
                json.decodeFromString(HeaderFooter.serializer(), serialized),
                "Deserialization failed",
            )
        assertEquals(headerFooter, deserialized, "Deserialized HeaderFooter does not match original")
    }
}
