package de.l4zs.html2pdfform.backend.util

import kotlin.test.Test

class RadioCalculationTest {
    @Test
    fun `test that optimal is not more than max`() {
        for (maxRadiosPerRow in 1..10) {
            for (count in 1..maxRadiosPerRow * 4) {
                assert(calculateOptimalRadiosPerRow(count, maxRadiosPerRow) <= maxRadiosPerRow) {
                    "Optimal is more than max"
                }
            }
        }
    }

    @Test
    fun `test that optimal does not cause more rows than before`() {
        for (maxRadiosPerRow in 1..10) {
            for (count in 1..maxRadiosPerRow * 4) {
                val maxRows = (count + maxRadiosPerRow - 1) / maxRadiosPerRow
                assert(calculateOptimalRadiosPerRow(count, maxRadiosPerRow) * maxRows >= count) {
                    "Optimal causes more rows than before"
                }
            }
        }
    }

    @Test
    fun `test some specific cases`() {
        // test some specific cases
        assert(calculateOptimalRadiosPerRow(6, 4) == 3) {
            "3-3 should be better than 4-2"
        }
        assert(calculateOptimalRadiosPerRow(8, 5) == 4) {
            "4-4 should be better than 5-3"
        }
        assert(calculateOptimalRadiosPerRow(7, 5) == 4) {
            "4-3 should be better than 5-2"
        }
        assert(calculateOptimalRadiosPerRow(11, 5) == 4) {
            "4-4-3 should be better than 5-5-1"
        }
    }
}
