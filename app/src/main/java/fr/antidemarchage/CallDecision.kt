package fr.antidemarchage

/**
 * Pure logic shared by the simulator and call screening service.
 * Test mode never interacts with TelecomManager or creates a call.
 */
object CallDecision {
    @JvmStatic
    fun evaluate(number: String?, enabled: Boolean, prefixEnabled: (String) -> Boolean): Result {
        val prefix = PrefixFilter.match(number)
        val blocked = enabled && prefix.isNotEmpty() && prefixEnabled(prefix)
        return Result(blocked, prefix)
    }

    data class Result(val blocked: Boolean, val prefix: String)
}
