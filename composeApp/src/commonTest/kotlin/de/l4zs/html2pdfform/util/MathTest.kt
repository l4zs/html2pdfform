package de.l4zs.html2pdfform.util

import java.util.*
import kotlin.test.Test

class MathTest {
    @Test
    fun `test point to inch`() {
        val inch = 72f.pointToInch()
        assert(inch == 1f) { "Expected 1 but was $inch" }
    }

    @Test
    fun `test inch to centimeter`() {
        val centimeter = 1f.inchToCentimeter()
        assert(centimeter == 2.54f) { "Expected 2.54 but was $centimeter" }
    }

    @Test
    fun `test string rounding`() {
        val number = 1.234567f
        Locale.setDefault(Locale.ENGLISH)
        var rounded = number.stringRoundedTo(2)
        assert(rounded == "1.23") { "Expected 1.23 but was $rounded" }
        Locale.setDefault(Locale.GERMAN)
        rounded = number.stringRoundedTo(2)
        assert(rounded == "1,23") { "Expected 1,23 but was $rounded" }
    }
}
