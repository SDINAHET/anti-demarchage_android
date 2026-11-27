package fr.antidemarchage

import org.junit.Assert.*
import org.junit.Test

class CallDecisionTest {
    @Test fun activeFilterBlocksOfficialPrefixes() {
        for (prefix in PrefixFilter.PREFIXES) {
            val value = prefix + "0".repeat(10 - prefix.length)
            val actual = CallDecision.evaluate(value, true) { true }
            assertTrue(value, actual.blocked)
            assertEquals(prefix, actual.prefix)
        }
    }

    @Test fun inactiveOrUnselectedPrefixNeverBlocks() {
        assertFalse(CallDecision.evaluate("0162457890", false) { true }.blocked)
        assertFalse(CallDecision.evaluate("0162457890", true) { false }.blocked)
        assertFalse(CallDecision.evaluate("0162457890", true) { it != "0162" }.blocked)
    }

    @Test fun unknownAndEmergencyNumbersPass() {
        for (number in listOf("0612345678", "0712345678", "0297123456", "0800112112",
                "15", "17", "18", "112", "", "private")) {
            assertFalse(number, CallDecision.evaluate(number, true) { true }.blocked)
        }
    }

    @Test fun internationalFormat() {
        val result = CallDecision.evaluate("+33 1 62 45 78 90", true) { true }
        assertTrue(result.blocked)
        assertEquals("0162", result.prefix)
    }
}
