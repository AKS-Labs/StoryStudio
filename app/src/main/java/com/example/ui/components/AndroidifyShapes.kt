package com.example.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Androidify soft marshmallow scalloped flower shape matching Screenshots 2 & 3.
 * Uses 10 smooth, puffy, rounded lobes without sharp valleys.
 */
class ScallopShape(
    private val lobes: Int = 10,
    private val depthRatio: Float = 0.065f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val baseRadius = minOf(centerX, centerY) * 0.94f
        val amplitude = baseRadius * depthRatio

        val steps = 240
        for (i in 0..steps) {
            val theta = (i.toFloat() / steps) * 2f * PI.toFloat()
            // Smooth sinusoidal modulation with dampening to ensure soft marshmallow bumps
            val wave = cos(lobes * theta)
            val r = baseRadius + amplitude * wave
            val x = centerX + r * cos(theta)
            val y = centerY + r * sin(theta)

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        return Outline.Generic(path)
    }
}

/**
 * Organic hand-drawn brush doodle shape for the onboarding hero screen matching Screenshot 1.
 */
class BrushDoodleShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val w = size.width
        val h = size.height

        // Hand-painted organic doodle blob with expressive curves
        path.moveTo(w * 0.50f, h * 0.08f)
        path.cubicTo(w * 0.78f, h * 0.04f, w * 0.96f, h * 0.22f, w * 0.92f, h * 0.44f)
        path.cubicTo(w * 0.98f, h * 0.65f, w * 0.88f, h * 0.88f, w * 0.65f, h * 0.92f)
        path.cubicTo(w * 0.48f, h * 0.96f, w * 0.32f, h * 0.91f, w * 0.16f, h * 0.86f)
        path.cubicTo(w * 0.04f, h * 0.72f, w * 0.02f, h * 0.52f, w * 0.08f, h * 0.36f)
        path.cubicTo(w * 0.04f, h * 0.18f, w * 0.25f, h * 0.06f, w * 0.50f, h * 0.08f)
        path.close()

        return Outline.Generic(path)
    }
}
