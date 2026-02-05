package com.rexus.bandselector.data

import android.content.Context
import android.content.pm.PackageManager
import android.telephony.CellInfoLte
import android.telephony.CellInfoNr
import android.telephony.CellSignalStrengthLte
import android.telephony.CellSignalStrengthNr
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.rexus.bandselector.data.model.NetworkType
import com.rexus.bandselector.data.model.SignalMetrics
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.Executors

class TelephonyRepo(private val context: Context) {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    fun observeSignalMetrics(): Flow<SignalMetrics> = callbackFlow {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            trySend(SignalMetrics(operator = "Permission Denied"))
            close()
            return@callbackFlow
        }

        val executor = Executors.newSingleThreadExecutor()
        val callback = object : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener, TelephonyCallback.DisplayInfoListener {
            override fun onSignalStrengthsChanged(signalStrength: android.telephony.SignalStrength) {
                // Parse the signal strength to find the primary serving cell
                // accurate parsing requires iterating cellInfo usually, but simple approach:
                
                val cellInfos = if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    telephonyManager.allCellInfo
                } else emptyList()

                // Find registered cell
                val primaryCell = cellInfos.firstOrNull { it.isRegistered }
                
                var metrics = SignalMetrics(
                    operator = telephonyManager.networkOperatorName ?: "Unknown"
                )

                if (primaryCell is CellInfoLte) {
                     metrics = metrics.copy(
                         type = NetworkType.LTE,
                         dbm = primaryCell.cellSignalStrength.rsrp,
                         quality = primaryCell.cellSignalStrength.rsrq,
                         band = "LTE Band Unknown" // Requires calculation from EARFCN which is in CellIdentity
                     )
                } else if (primaryCell is CellInfoNr) {
                     val ss = primaryCell.cellSignalStrength as? CellSignalStrengthNr
                     metrics = metrics.copy(
                         type = NetworkType.NR_SA,
                         dbm = ss?.ssRsrp ?: -140,
                         quality = ss?.ssRsrq ?: 0,
                         band = "5G NR"
                     )
                } else {
                    // Fallback to legacy SignalStrength object
                    val lte = signalStrength.cellSignalStrengths.filterIsInstance<CellSignalStrengthLte>().firstOrNull()
                    if (lte != null) {
                        metrics = metrics.copy(type = NetworkType.LTE, dbm = lte.rsrp, quality = lte.rsrq)
                    }
                }
                
                trySend(metrics)
            }

            override fun onDisplayInfoChanged(telephonyDisplayInfo: android.telephony.TelephonyDisplayInfo) {
                // Handle 5G icon logic (NR vs LTE_CA)
            }
        }

        telephonyManager.registerTelephonyCallback(executor, callback)

        awaitClose {
            telephonyManager.unregisterTelephonyCallback(callback)
            executor.shutdown()
        }
    }
}
