package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlumniBlue
import com.example.ui.theme.AlumniGold
import com.example.ui.theme.AlumniGoldAccent
import com.example.ui.theme.AlumniNavyDark
import kotlinx.coroutines.delay

@Composable
fun ViswamIntroScreen(
    onFinished: () -> Unit
) {
    // Entry animations
    val scaleAnim = remember { Animatable(0.4f) }
    val alphaAnim = remember { Animatable(0f) }
    val rotXAnim = remember { Animatable(35f) }
    val rotYAnim = remember { Animatable(-30f) }

    // Continuous floating 3D tilt
    val infiniteTransition = rememberInfiniteTransition(label = "3d_float")
    val tiltY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt_y"
    )
    val floatY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    LaunchedEffect(Unit) {
        // Dramatic 3D swing in
        alphaAnim.animateTo(1f, tween(600))
        scaleAnim.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
        rotXAnim.animateTo(8f, tween(900, easing = FastOutSlowInEasing))
        rotYAnim.animateTo(0f, tween(900, easing = FastOutSlowInEasing))

        // Auto advance after 3.5 seconds
        delay(3200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E3A8A),
                        Color(0xFF0F172A),
                        Color(0xFF020617)
                    )
                )
            )
            .clickable { onFinished() }
            .testTag("viswam_intro_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Ambient background glowing circles
        Box(
            modifier = Modifier
                .size(340.dp)
                .scale(glowPulse)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AlumniGoldAccent.copy(alpha = 0.22f),
                            AlumniBlue.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .offset(y = floatY.dp)
                .graphicsLayer {
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                    alpha = alphaAnim.value
                    rotationX = rotXAnim.value
                    rotationY = tiltY
                    cameraDistance = 14f * density
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // School Crest Icon with 3D elevation
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = AlumniNavyDark),
                border = androidx.compose.foundation.BorderStroke(3.dp, AlumniGoldAccent),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer {
                        rotationZ = tiltY * 0.5f
                    }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = AlumniGoldAccent,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 3D Extruded Letters: "VISWAM"
            ThreeDWordText(
                word = "VISWAM",
                baseColor = Color(0xFFFBBF24),
                shadowColor = Color(0xFF78350F),
                fontSize = 46
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3D Extruded Letters: "SCHOOL"
            ThreeDWordText(
                word = "SCHOOL",
                baseColor = Color(0xFF60A5FA),
                shadowColor = Color(0xFF1E3A8A),
                fontSize = 32
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 3D Metallic Pill Badge: "CBSE 7A"
            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    Brush.horizontalGradient(listOf(AlumniGoldAccent, Color(0xFF38BDF8), AlumniGoldAccent))
                ),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .graphicsLayer {
                        rotationX = -8f
                        rotationY = tiltY * 0.8f
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AlumniGoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CBSE 7A",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp,
                        style = TextStyle(
                            shadow = Shadow(
                                color = AlumniGoldAccent,
                                offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                                blurRadius = 6f
                            )
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Official Classmate Alumni Hub",
                color = Color.LightGray.copy(alpha = 0.8f),
                fontSize = 13.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Skip / Continue button at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        ) {
            Button(
                onClick = onFinished,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E293B).copy(alpha = 0.85f),
                    contentColor = AlumniGoldAccent
                ),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AlumniGoldAccent.copy(alpha = 0.5f)),
                modifier = Modifier.testTag("skip_3d_intro_button")
            ) {
                Text(
                    text = "Tap to Enter Hub →",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * Renders letters with 3D stacked extrusion effect (layered depth shadows)
 */
@Composable
fun ThreeDWordText(
    word: String,
    baseColor: Color,
    shadowColor: Color,
    fontSize: Int
) {
    Box(contentAlignment = Alignment.Center) {
        // Deep 3D extrusion layers
        for (i in 6 downTo 1) {
            val offset = (i * 1.6).dp
            Text(
                text = word,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Black,
                color = shadowColor.copy(alpha = 0.85f),
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(x = offset, y = offset)
            )
        }

        // Front Face with gradient & highlight
        Text(
            text = word,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Black,
            color = baseColor,
            letterSpacing = 3.sp,
            textAlign = TextAlign.Center,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.7f),
                    offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )
    }
}
