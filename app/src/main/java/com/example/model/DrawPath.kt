package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

enum class BrushStyle {
    PEN,
    HIGHLIGHTER,
    NEON_GLOW,
    ERASER
}

data class DrawPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val brushStyle: BrushStyle = BrushStyle.PEN
)

enum class CaptionStyle {
    ROUNDED_PILL,
    SOLID_BLOCK,
    OUTLINE,
    TRANSPARENT
}

data class StoryCaption(
    val text: String = "",
    val textColor: Color = Color.White,
    val backgroundColor: Color = Color(0xCC000000),
    val style: CaptionStyle = CaptionStyle.ROUNDED_PILL,
    val offset: Offset = Offset(0f, 0f),
    val isVisible: Boolean = true
)

enum class CanvasTool {
    NONE,
    DRAW,
    STICKER,
    CAPTION
}
