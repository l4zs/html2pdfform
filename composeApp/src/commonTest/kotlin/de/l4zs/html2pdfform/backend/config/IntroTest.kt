package de.l4zs.html2pdfform.backend.config

import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IntroTest {
    private lateinit var introImage: IntroImage
    private lateinit var introText: IntroText
    private lateinit var intro: Intro

    @BeforeTest
    fun setup() {
        introImage = IntroImage()
        introText = IntroText()
        intro = Intro(image = introImage, text = introText)
    }

    @Test
    fun `test image (de-)serializing`() {
        val json = Json
        val serialized =
            assertIs<String>(json.encodeToString(IntroImage.serializer(), introImage), "Serialization failed")
        require(serialized.isNotBlank()) { "Serialized IntroImage is blank" }
        val deserialized =
            assertIs<IntroImage>(json.decodeFromString(IntroImage.serializer(), serialized), "Deserialization failed")
        assertEquals(introImage, deserialized, "Deserialized IntroImage does not match original")
    }

    @Test
    fun `test text (de-)serializing`() {
        val json = Json
        val serialized =
            assertIs<String>(json.encodeToString(IntroText.serializer(), introText), "Serialization failed")
        require(serialized.isNotBlank()) { "Serialized IntroText is blank" }
        val deserialized =
            assertIs<IntroText>(json.decodeFromString(IntroText.serializer(), serialized), "Deserialization failed")
        assertEquals(introText, deserialized, "Deserialized IntroText does not match original")
    }

    @Test
    fun `test intro (de-)serializing`() {
        val json = Json
        val serialized = assertIs<String>(json.encodeToString(Intro.serializer(), intro), "Serialization failed")
        require(serialized.isNotBlank()) { "Serialized Intro is blank" }
        val deserialized =
            assertIs<Intro>(json.decodeFromString(Intro.serializer(), serialized), "Deserialization failed")
        assertEquals(intro, deserialized, "Deserialized Intro does not match original")
    }
}
