package de.l4zs.html2pdfform.backend.data.field

import de.l4zs.html2pdfform.backend.converter.convert
import de.l4zs.html2pdfform.backend.data.Context
import de.l4zs.html2pdfform.backend.data.createTestContext
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CheckboxTest {
    private lateinit var context: Context
    private lateinit var checkbox: FieldWithLabel<Checkbox>

    @Before
    fun setUp() {
        context = createTestContext()
        val html =
            """
            <form>
                <label for="testId">Test</label>
                <input type="checkbox" name="test" id="testId" value="1" toggles="otherId" checked>
            </form>
            """.trimIndent()
        val element = Jsoup.parse(html)
        checkbox =
            assertIs<FieldWithLabel<Checkbox>>(
                runBlocking {
                    element.convert(context)?.first()
                },
            )
    }

    @Test
    fun `test that checkbox has correct value`() {
        assertEquals("1", checkbox.formField.value)
    }

    @Test
    fun `test that checkbox has correct mapping name`() {
        assertEquals("testId", checkbox.formField.mappingName)
    }

    @Test
    fun `test that checkbox has correct toggles`() {
        assertEquals(listOf("otherId"), checkbox.formField.toggles)
    }

    @Test
    fun `test that checkbox has correct label`() {
        assertEquals("Test", checkbox.label?.value)
        assertEquals("Test", checkbox.label?.text?.content)
    }
}
