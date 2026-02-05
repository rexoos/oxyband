package com.rexus.bandselector.data.vendor

import com.rexus.bandselector.domain.BandController

object VendorFactory {
    suspend fun getController(): BandController {
        val qc = QualcommStrategy()
        if (qc.isSupported()) return qc
        
        val mtk = MediaTekStrategy()
        if (mtk.isSupported()) return mtk
        
        return object : BandController {
            override suspend fun isSupported() = false
            override suspend fun lockBands(rat: com.rexus.bandselector.data.model.NetworkType, bands: List<Int>) = Result.failure<Unit>(Exception("Unsupported Vendor"))
            override suspend fun resetNetwork() = Result.success(Unit)
            override suspend fun restartRadio() = Result.success(Unit)
        }
    }
}
