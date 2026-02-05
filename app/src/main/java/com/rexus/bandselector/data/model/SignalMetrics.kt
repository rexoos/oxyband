package com.rexus.bandselector.data.model

data class SignalMetrics(
    val type: NetworkType = NetworkType.UNKNOWN,
    val dbm: Int = -140, // Signal Strength (RSRP for LTE/NR)
    val quality: Int = 0, // RSRQ or SNR
    val band: String = "Unknown", // E.g., "LTE 3", "NR 78"
    val cellId: Long = 0,
    val operator: String = "Unknown"
)

enum class NetworkType {
    UNKNOWN, GSM, UMTS, LTE, NR_NSA, NR_SA
}
