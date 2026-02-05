package com.rexus.bandselector.domain.engine

import com.rexus.bandselector.data.model.NetworkType
import com.rexus.bandselector.data.model.SignalMetrics

class RecommendationEngine {

    data class Recommendation(
        val recommendedBands: List<Int>,
        val reason: String,
        val type: NetworkType
    )

    fun analyze(metrics: SignalMetrics): Recommendation {
        // Simple logic v1: RSRP vs Frequency analysis
        
        val rsrp = metrics.dbm
        val quality = metrics.quality
        
        // Scenario 1: Critical Signal (-115dBm or worse)
        // Recommendation: Drop to Low Bands (Long Range)
        if (rsrp < -115) {
            return Recommendation(
                recommendedBands = listOf(12, 13, 17, 71, 20, 28, 5, 8), // Common Low Bands
                reason = "Critical Signal Strength detected ($rsrp dBm). Switching to Low-Frequency Bands (700/800/900 MHz) for improved penetration and connection stability.",
                type = NetworkType.LTE
            )
        }
        
        // Scenario 2: Good Signal but Poor Quality (Congestion/Interference)
        if (rsrp > -95 && quality < -15) {
             return Recommendation(
                recommendedBands = listOf(66, 4, 2, 7, 3, 1, 41, 77, 78), // Mid/High
                reason = "Signal is strong but quality is poor (Interference). Suggesting alternate Mid/High bands to evade congestion on the current frequency.",
                type = NetworkType.LTE
             )
        }

        // Scenario 3: Excellent Conditions (Speed Mode)
        if (rsrp > -85 && quality > -10) {
             return Recommendation(
                recommendedBands = listOf(41, 77, 78, 257, 258, 260, 261), // TDD / mmWave
                reason = "Excellent conditions. Prioritizing High-Capacity TDD / 5G bands for maximum throughput.",
                type = NetworkType.NR_SA
             )
        }

        return Recommendation(
            recommendedBands = emptyList(), // Auto
            reason = "Network conditions are balanced. Automatic selection is recommended.",
            type = NetworkType.UNKNOWN
        )
    }
}
