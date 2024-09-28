package de.l4zs.html2pdfform.backend.data

import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageTest {
    @Test
    fun `test language to locale`() {
        assertEquals(Locale.ENGLISH, Language.ENGLISH.toLocale())
        assertEquals(Locale.GERMAN, Language.GERMAN.toLocale())
    }

    @Test
    fun `test language translations`() {
        Locale.setDefault(Locale.ENGLISH)
        var english = runBlocking { getString(Language.ENGLISH.resource) }
        var german = runBlocking { getString(Language.GERMAN.resource) }
        assertEquals("English", english, "Expected english translation of 'English'")
        assertEquals("German", german, "Expected english translation of 'German'")
        Locale.setDefault(Locale.GERMAN)
        english = runBlocking { getString(Language.ENGLISH.resource) }
        german = runBlocking { getString(Language.GERMAN.resource) }
        assertEquals("Englisch", english, "Expected german translation of 'English'")
        assertEquals("Deutsch", german, "Expected german translation of 'German'")
    }
}
