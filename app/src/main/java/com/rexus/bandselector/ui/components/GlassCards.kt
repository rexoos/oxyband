package com.rexus.bandselector.ui.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rexus.bandselector.ui.theme.GlassWhite
import com.rexus.bandselector.ui.theme.SurfaceGrey

@Composable
fun OxygenGlassCard(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 30.dp,
    content: @Composable () -> Unit
) {
    // RenderEffect is Android 12 (S) +
    val isBlurSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .graphicsLayer {
                if (isBlurSupported) {
                    renderEffect = RenderEffect
                        .createBlurEffect(
                            blurRadius.toPx(), 
                            blurRadius.toPx(), 
                            Shader.TileMode.MIRROR
                        )
                        .asComposeRenderEffect()
                }
                // Clip purely for the blur
                clip = true
                componentAlpha = 0.99f // Force layer
            }
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Transparent) // The blur must see what's behind
    ) {
        // Overlay Layer (The "Glass" Tint)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), // Fallback or Tint
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}
