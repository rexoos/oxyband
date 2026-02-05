package com.rexus.bandselector.domain

import com.rexus.bandselector.data.model.NetworkType

interface BandController {
    /**
     * Checks if the device is supported and root is available.
     */
    suspend fun isSupported(): Boolean

    /**
     * Locks the modem to specific RAT and Bands.
     * @param rat The Radio Access Technology (LTE, NR, GSM).
     * @param bands List of band numbers (e.g., [3, 7, 20] for LTE B3/B7/B20).
     */
    suspend fun lockBands(rat: NetworkType, bands: List<Int>): Result<Unit>

    /**
     * Resets the modem to factory/auto selection.
     */
    suspend fun resetNetwork(): Result<Unit>
    
    /**
     * Restarts the radio stack (Airplane Mode toggle or equivalent).
     */
    suspend fun restartRadio(): Result<Unit>
}
