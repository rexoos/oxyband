package com.rexus.bandselector.data.util

import java.math.BigInteger

object BandMaskUtils {

    /**
     * Converts a list of LTE Band Numbers (1-64+) into a Hex String for NV Items.
     * NV 00028874 is typically 64-bit.
     */
    fun calculateLteMask(bands: List<Int>): String {
        if (bands.isEmpty()) return "0"
        
        var mask = BigInteger.ZERO
        bands.forEach { band ->
            // Band 1 is bit 0, so shift by (band - 1)
            if (band > 0) {
                val bit = BigInteger.ONE.shiftLeft(band - 1)
                mask = mask.or(bit)
            }
        }
        return mask.toString(16) // Returns Hex
    }

    /**
     * Converts a list of NR Band Numbers (n1, n78, etc.) into a Hex String.
     * NV 00065633 structure varies but usually follows the same bit index logic.
     */
    fun calculateNrMask(bands: List<Int>): String {
         if (bands.isEmpty()) return "0"
        
        var mask = BigInteger.ZERO
        bands.forEach { band ->
            if (band > 0) {
                val bit = BigInteger.ONE.shiftLeft(band - 1)
                mask = mask.or(bit)
            }
        }
        return mask.toString(16)
    }

    /**
     * Parsing Hex back to human readable bands (for verifying current state).
     */
    fun parseLteMask(hex: String?): List<Int> {
        if (hex.isNullOrEmpty()) return emptyList()
        
        val bands = mutableListOf<Int>()
        try {
            val mask = BigInteger(hex, 16)
            for (i in 0..127) { // Check reasonable range
                if (mask.testBit(i)) {
                    bands.add(i + 1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bands
    }
}
