package com.rexus.bandselector.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rexus.bandselector.ui.components.OxygenGlassCard
import com.rexus.bandselector.ui.theme.LiquidGradientStart
import com.rexus.bandselector.ui.theme.SignalGood
import com.rexus.bandselector.ui.theme.SignalModerate
import com.rexus.bandselector.ui.theme.SignalPoor

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val metrics by viewModel.metrics.collectAsState()
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (!hasPermission) {
            launcher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
            ))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        Color(0xFF0A1018)
                    )
                )
            )
    ) {
        // Ambient Mesh
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            LiquidGradientStart.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        radius = 1200f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Band Selector",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            
            Text(
                text = "System Monitor",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Status Card
            OxygenGlassCard {
                Column {
                    Text("Connection Status", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Crossfade(targetState = metrics.operator, label = "OperatorFade") { op ->
                        Text(
                            text = if (op == "Unknown" && metrics.dbm == -140) "Searching..." else op,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                    
                    val animatedDbm by animateIntAsState(
                        targetValue = metrics.dbm,
                        animationSpec = tween(500, easing = FastOutSlowInEasing),
                        label = "DbmAnim"
                    )
                    
                    val signalColor = when {
                        animatedDbm > -85 -> SignalGood
                        animatedDbm > -105 -> SignalModerate
                        else -> SignalPoor
                    }
                    
                    Text(
                        text = "Signal: ${animatedDbm} dBm", 
                        color = signalColor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Band Info
            OxygenGlassCard {
                Column {
                    Text("Current Band", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = metrics.band,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Network: ${metrics.type.name}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
