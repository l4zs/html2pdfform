package de.l4zs.html2pdfform.backend.config

import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MetadataTest {
    private lateinit var metadata: Metadata

    @BeforeTest
    fun setup() {
        metadata = Metadata("author", "creator", "subject")
    }

    @Test
    fun `test metadata (de-)serializing`() {
        val json = Json
        val serialized = assertIs<String>(json.encodeToString(Metadata.serializer(), metadata), "Serialization failed")
        require(serialized.isNotBlank()) { "Serialized Metadata is blank" }
        val deserialized =
            assertIs<Metadata>(json.decodeFromString(Metadata.serializer(), serialized), "Deserialization failed")
        assertEquals(metadata, deserialized, "Deserialized Metadata does not match original")
    }
}
