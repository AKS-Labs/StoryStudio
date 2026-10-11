package com.example.canvas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrushStyle
import com.example.ui.theme.AndroidifyBlack
import com.example.ui.theme.AndroidifyGreen
import com.example.ui.theme.AndroidifySurface
import com.example.ui.theme.PaletteAndroidGreen
import com.example.ui.theme.PaletteCyberYellow
import com.example.ui.theme.PaletteElectricViolet
import com.example.ui.theme.PaletteGoogleBlue
import com.example.ui.theme.PaletteHotPink
import com.example.ui.theme.PaletteMidnight
import com.example.ui.theme.PaletteTangerine
import com.example.ui.theme.PaletteTeal

@Composable
fun BrushControlBar(
    isVisible: Boolean,
    currentStyle: BrushStyle,
    currentColor: Color,
    currentStrokeWidth: Float,
    onStyleChanged: (BrushStyle) -> Unit,
    onColorChanged: (Color) -> Unit,
    onStrokeWidthChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = listOf(
        PaletteAndroidGreen,
        PaletteGoogleBlue,
        PaletteCyberYellow,
        PaletteTangerine,
        PaletteHotPink,
        PaletteElectricViolet,
        PaletteTeal,
        PaletteMidnight
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("brush_control_bar"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AndroidifySurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFE0E5E0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Brush Style Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val brushTypes = listOf(
                        Pair(BrushStyle.PEN, "Pen" to Icons.Default.Edit),
                        Pair(BrushStyle.HIGHLIGHTER, "Marker" to Icons.Default.Brush),
                        Pair(BrushStyle.NEON_GLOW, "Glow" to Icons.Default.AutoAwesome),
                        Pair(BrushStyle.ERASER, "Eraser" to Icons.Default.CleaningServices)
                    )

                    brushTypes.forEach { (style, meta) ->
                        val (label, icon) = meta
                        val isSelected = currentStyle == style

                        FilterChip(
                            selected = isSelected,
                            onClick = { onStyleChanged(style) },
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AndroidifyBlack,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White,
                                containerColor = Color(0xFFF0F5F0),
                                labelColor = AndroidifyBlack,
                                iconColor = AndroidifyBlack
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // If not in eraser mode, show color swatches
                if (currentStyle != BrushStyle.ERASER) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        palette.forEach { color ->
                            val isSelected = currentColor == color
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(color, CircleShape)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) AndroidifyBlack else Color(0xFFD0D7D0),
                                        shape = CircleShape
                                    )
                                    .clickable { onColorChanged(color) }
                                    .testTag("color_swatch_${color.value}")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Brush Stroke Size Slider with Preview Bubble
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFF0F5F0), CircleShape)
                            .border(1.dp, Color(0xFFE0E5E0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size((currentStrokeWidth * 0.65f).coerceIn(4f, 24f).dp)
                                .background(if (currentStyle == BrushStyle.ERASER) Color.White else currentColor, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Slider(
                        value = currentStrokeWidth,
                        onValueChange = onStrokeWidthChanged,
                        valueRange = 4f..40f,
                        colors = SliderDefaults.colors(
                            thumbColor = AndroidifyBlack,
                            activeTrackColor = AndroidifyGreen,
                            inactiveTrackColor = Color(0xFFD8E0D8)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${currentStrokeWidth.toInt()}px",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AndroidifyBlack
                    )
                }
            }
        }
    }
}
