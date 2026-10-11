package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun QrCodeView(
    token: String,
    modifier: Modifier = Modifier,
    sizeDp: Int = 180
) {
    // Generate deterministic 21x21 QR pattern based on token string hash & character codes
    val grid = remember(token) {
        val n = 21
        val matrix = Array(n) { BooleanArray(n) }

        fun fillFinderPattern(startR: Int, startC: Int) {
            for (r in 0 until 7) {
                for (c in 0 until 7) {
                    val isBorder = r == 0 || r == 6 || c == 0 || c == 6
                    val isCenter = r in 2..4 && c in 2..4
                    matrix[startR + r][startC + c] = isBorder || isCenter
                }
            }
        }

        // Top-left, Top-right, Bottom-left finder patterns
        fillFinderPattern(0, 0)
        fillFinderPattern(0, n - 7)
        fillFinderPattern(n - 7, 0)

        // Timing patterns
        for (i in 7 until n - 7) {
            matrix[6][i] = (i % 2 == 0)
            matrix[i][6] = (i % 2 == 0)
        }

        // Deterministic payload encoding
        val bytes = token.toByteArray()
        var byteIdx = 0
        for (r in 0 until n) {
            for (c in 0 until n) {
                // skip finder patterns
                val inTopLeft = r < 8 && c < 8
                val inTopRight = r < 8 && c >= n - 8
                val inBottomLeft = r >= n - 8 && c < 8
                val inTiming = r == 6 || c == 6

                if (!inTopLeft && !inTopRight && !inBottomLeft && !inTiming) {
                    val b = if (bytes.isNotEmpty()) bytes[byteIdx % bytes.size].toInt() else 0
                    val bitVal = ((b xor (r * 13 + c * 7)) shr (c % 8)) and 1
                    matrix[r][c] = (bitVal == 1)
                    byteIdx++
                }
            }
        }
        matrix
    }

    Surface(
        modifier = modifier
            .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(14.dp),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Canvas(
                modifier = Modifier
                    .size(sizeDp.dp)
                    .background(Color.White)
            ) {
                val n = 21
                val cellSize = size.width / n
                for (r in 0 until n) {
                    for (c in 0 until n) {
                        if (grid[r][c]) {
                            drawRect(
                                color = Color(0xFF0F172A),
                                topLeft = Offset(c * cellSize, r * cellSize),
                                size = Size(cellSize + 0.5f, cellSize + 0.5f)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = token,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                letterSpacing = 1.sp
            )
            Text(
                text = "Show this QR code at pickup counter",
                fontSize = 10.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
