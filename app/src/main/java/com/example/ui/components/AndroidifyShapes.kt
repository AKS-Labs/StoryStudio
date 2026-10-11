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
 * Androidify soft marshmallow scalloped shape with 100% curved, rounded bumps.
 * Uses cubic Bézier arcs with tangential control points so peaks and valleys
 * are pillowy, smooth, and completely devoid of sharp spikes.
 */
class ScallopShape(
    private val lobes: Int = 8,
    private val depthRatio: Float = 0.055f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rBase = minOf(cx, cy) * 0.94f
        val rOuter = rBase
        val rInner = rBase * (1f - depthRatio)

        val totalPoints = lobes * 2 // alternating outer peaks and inner valleys
        val angleStep = (2f * PI / totalPoints).toFloat()

        // Generate smooth cubic Bézier spline connecting rounded peaks and valleys
        for (i in 0 until totalPoints) {
            val a0 = i * angleStep
            val a1 = (i + 1) * angleStep

            val isPeak0 = i % 2 == 0
            val r0 = if (isPeak0) rOuter else rInner
            val r1 = if (isPeak0) rInner else rOuter

            val x0 = cx + r0 * cos(a0)
            val y0 = cy + r0 * sin(a0)
            val x1 = cx + r1 * cos(a1)
            val y1 = cy + r1 * sin(a1)

            if (i == 0) {
                path.moveTo(x0, y0)
            }

            // Tangential control points ensuring smooth rounded curvature at every vertex
            val handleLen = (r0 + r1) / 2f * angleStep * 0.55f
            val cp1x = x0 - handleLen * sin(a0)
            val cp1y = y0 + handleLen * cos(a0)
            val cp2x = x1 + handleLen * sin(a1)
            val cp2y = y1 - handleLen * cos(a1)

            path.cubicTo(cp1x, cp1y, cp2x, cp2y, x1, y1)
        }
        path.close()
        return Outline.Generic(path)
    }
}
