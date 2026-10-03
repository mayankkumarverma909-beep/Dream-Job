package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedInterviewerAvatar(
    isSpeaking: Boolean = false,
    isListening: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Pulse animation for avatar aura when active
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isSpeaking || isListening) 1.08f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val waveHeight by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_height"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(140.dp)
        ) {
            // Glowing outer ripple
            Box(
                modifier = Modifier
                    .size((120 * pulseScale).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (isListening) listOf(Color(0x5510B981), Color.Transparent)
                            else if (isSpeaking) listOf(Color(0x666366F1), Color.Transparent)
                            else listOf(Color(0x334F46E5), Color.Transparent)
                        )
                    )
            )

            // Inner Avatar Body Canvas
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4338CA))
                        )
                    )
                    .border(3.dp, if (isListening) Color(0xFF10B981) else Color(0xFF818CF8), CircleShape)
            ) {
                // Procedural stylized interviewer face
                Canvas(modifier = Modifier.size(90.dp)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    // Head shape
                    drawCircle(
                        color = Color(0xFFFFDFC4),
                        radius = 28.dp.toPx(),
                        center = Offset(center.x, center.y - 4.dp.toPx())
                    )
                    // Professional Hair
                    drawArc(
                        color = Color(0xFF1E293B),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(center.x - 28.dp.toPx(), center.y - 32.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(56.dp.toPx(), 40.dp.toPx())
                    )
                    // Eyes
                    val eyeRadius = 3.dp.toPx()
                    drawCircle(
                        color = Color(0xFF0F172A),
                        radius = eyeRadius,
                        center = Offset(center.x - 10.dp.toPx(), center.y - 6.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF0F172A),
                        radius = eyeRadius,
                        center = Offset(center.x + 10.dp.toPx(), center.y - 6.dp.toPx())
                    )
                    // Gentle Eyebrows
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(center.x - 14.dp.toPx(), center.y - 12.dp.toPx()),
                        end = Offset(center.x - 6.dp.toPx(), center.y - 12.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(center.x + 6.dp.toPx(), center.y - 12.dp.toPx()),
                        end = Offset(center.x + 14.dp.toPx(), center.y - 12.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                    // Mouth: Animates when speaking
                    if (isSpeaking) {
                        drawOval(
                            color = Color(0xFFDC2626),
                            topLeft = Offset(center.x - 6.dp.toPx(), center.y + 4.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(12.dp.toPx(), 8.dp.toPx())
                        )
                    } else {
                        // Friendly neutral smile
                        drawArc(
                            color = Color(0xFF334155),
                            startAngle = 20f,
                            sweepAngle = 140f,
                            useCenter = false,
                            topLeft = Offset(center.x - 8.dp.toPx(), center.y + 2.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(16.dp.toPx(), 8.dp.toPx())
                        )
                    }
                    // Professional Blazer Shoulders
                    drawArc(
                        color = Color(0xFF0F172A),
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(center.x - 38.dp.toPx(), center.y + 14.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(76.dp.toPx(), 44.dp.toPx())
                    )
                }
            }

            // Live status badge
            Surface(
                shape = CircleShape,
                color = if (isListening) Color(0xFF10B981) else if (isSpeaking) Color(0xFF4F46E5) else Color(0xFF64748B),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-8).dp, y = (-8).dp)
                    .size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else if (isSpeaking) Icons.Default.GraphicEq else Icons.Default.Videocam,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Dr. Priya Nair (AI Interviewer)",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = if (isSpeaking) "Speaking question..." else if (isListening) "Listening to your answer..." else "Ready for next response",
            fontSize = 12.sp,
            color = if (isListening) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}
