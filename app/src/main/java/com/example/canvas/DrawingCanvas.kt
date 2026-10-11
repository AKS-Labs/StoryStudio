package com.example.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.model.BrushStyle
import com.example.model.DrawPath

@Composable
fun DrawingCanvas(
    isDrawingActive: Boolean,
    currentColor: Color,
    currentStrokeWidth: Float,
    currentBrushStyle: BrushStyle,
    paths: List<DrawPath>,
    onPathCompleted: (DrawPath) -> Unit,
    modifier: Modifier = Modifier
) {
    var inProgressPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    val updatedIsDrawingActive by rememberUpdatedState(isDrawingActive)
    val updatedColor by rememberUpdatedState(currentColor)
    val updatedStrokeWidth by rememberUpdatedState(currentStrokeWidth)
    val updatedBrushStyle by rememberUpdatedState(currentBrushStyle)
    val updatedOnPathCompleted by rememberUpdatedState(onPathCompleted)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (isDrawingActive) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { startOffset ->
                                if (updatedIsDrawingActive) {
                                    inProgressPoints = listOf(startOffset)
                                }
                            },
                            onDrag = { change, _ ->
                                if (updatedIsDrawingActive) {
                                    change.consume()
                                    inProgressPoints = inProgressPoints + change.position
                                }
                            },
                            onDragEnd = {
                                if (inProgressPoints.size > 1) {
                                    updatedOnPathCompleted(
                                        DrawPath(
                                            points = inProgressPoints,
                                            color = updatedColor,
                                            strokeWidth = updatedStrokeWidth,
                                            brushStyle = updatedBrushStyle
                                        )
                                    )
                                }
                                inProgressPoints = emptyList()
                            },
                            onDragCancel = {
                                inProgressPoints = emptyList()
                            }
                        )
                    }
                } else Modifier
            )
    ) {
        // Draw completed strokes
        for (drawPath in paths) {
            renderStroke(drawPath)
        }

        // Draw active in-progress stroke
        if (inProgressPoints.size > 1) {
            renderStroke(
                DrawPath(
                    points = inProgressPoints,
                    color = currentColor,
                    strokeWidth = currentStrokeWidth,
                    brushStyle = currentBrushStyle
                )
            )
        }
    }
}

private fun DrawScope.renderStroke(drawPath: DrawPath) {
    if (drawPath.points.size < 2) return

    val path = Path().apply {
        moveTo(drawPath.points.first().x, drawPath.points.first().y)
        for (i in 1 until drawPath.points.size) {
            val p0 = drawPath.points[i - 1]
            val p1 = drawPath.points[i]
            val midX = (p0.x + p1.x) / 2f
            val midY = (p0.y + p1.y) / 2f
            quadraticTo(p0.x, p0.y, midX, midY)
        }
        val last = drawPath.points.last()
        lineTo(last.x, last.y)
    }

    when (drawPath.brushStyle) {
        BrushStyle.PEN -> {
            drawPath(
                path = path,
                color = drawPath.color,
                style = Stroke(
                    width = drawPath.strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
        BrushStyle.HIGHLIGHTER -> {
            drawPath(
                path = path,
                color = drawPath.color.copy(alpha = 0.5f),
                style = Stroke(
                    width = drawPath.strokeWidth * 2.2f,
                    cap = StrokeCap.Square,
                    join = StrokeJoin.Miter
                )
            )
        }
        BrushStyle.NEON_GLOW -> {
            // Ambient outer glow
            drawPath(
                path = path,
                color = drawPath.color.copy(alpha = 0.35f),
                style = Stroke(
                    width = drawPath.strokeWidth * 3.2f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            // Mid glow
            drawPath(
                path = path,
                color = drawPath.color.copy(alpha = 0.75f),
                style = Stroke(
                    width = drawPath.strokeWidth * 1.8f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            // Crisp white core
            drawPath(
                path = path,
                color = Color.White,
                style = Stroke(
                    width = drawPath.strokeWidth * 0.7f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
        BrushStyle.ERASER -> {
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.95f),
                style = Stroke(
                    width = drawPath.strokeWidth * 3.5f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                ),
                blendMode = BlendMode.SrcOver
            )
        }
    }
}
