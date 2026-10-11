package com.example.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import com.example.model.BrushStyle
import com.example.model.CaptionStyle
import com.example.model.CanvasSticker
import com.example.model.DrawPath
import com.example.model.StickerShape
import com.example.model.StoryCaption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max

object StoryExporter {

    private const val STORY_WIDTH = 1080
    private const val STORY_HEIGHT = 1920

    suspend fun exportStoryComposite(
        context: Context,
        basePhotoUri: Uri?,
        paths: List<DrawPath>,
        stickers: List<CanvasSticker>,
        caption: StoryCaption,
        viewWidth: Float,
        viewHeight: Float
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val scaleX = if (viewWidth > 0f) STORY_WIDTH / viewWidth else 1f
            val scaleY = if (viewHeight > 0f) STORY_HEIGHT / viewHeight else 1f
            val scaleFactor = max(scaleX, scaleY)

            val compositeBitmap = Bitmap.createBitmap(
                STORY_WIDTH,
                STORY_HEIGHT,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(compositeBitmap)

            // 1. Draw base photo or fallback gradient background
            drawBasePhoto(context, canvas, basePhotoUri)

            // 2. Draw freehand drawing paths
            drawVectorPaths(canvas, paths, scaleX, scaleY)

            // 3. Draw transformed stickers
            drawStickers(canvas, stickers, scaleX, scaleY)

            // 4. Draw caption if present
            if (caption.text.isNotBlank()) {
                drawCaption(canvas, caption)
            }

            // Save to shared cache file via FileProvider
            val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
            val storyFile = File(imagesDir, "story_${System.currentTimeMillis()}.png")

            FileOutputStream(storyFile).use { out ->
                compositeBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                storyFile
            )

            Result.success(contentUri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun drawBasePhoto(context: Context, canvas: Canvas, photoUri: Uri?) {
        var baseBitmap: Bitmap? = null
        if (photoUri != null) {
            try {
                val input: InputStream? = context.contentResolver.openInputStream(photoUri)
                baseBitmap = BitmapFactory.decodeStream(input)
                input?.close()
            } catch (e: Exception) {
                baseBitmap = null
            }
        }

        if (baseBitmap != null) {
            val matrix = Matrix()
            val srcWidth = baseBitmap.width.toFloat()
            val srcHeight = baseBitmap.height.toFloat()

            val scale = max(STORY_WIDTH / srcWidth, STORY_HEIGHT / srcHeight)
            val dx = (STORY_WIDTH - srcWidth * scale) / 2f
            val dy = (STORY_HEIGHT - srcHeight * scale) / 2f

            matrix.postScale(scale, scale)
            matrix.postTranslate(dx, dy)

            val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
            canvas.drawBitmap(baseBitmap, matrix, paint)
        } else {
            // Gradient background fallback
            val paint = Paint().apply {
                shader = android.graphics.LinearGradient(
                    0f, 0f, 0f, STORY_HEIGHT.toFloat(),
                    intArrayOf(
                        android.graphics.Color.parseColor("#121316"),
                        android.graphics.Color.parseColor("#261B4D"),
                        android.graphics.Color.parseColor("#0E0F14")
                    ),
                    null,
                    android.graphics.Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, STORY_WIDTH.toFloat(), STORY_HEIGHT.toFloat(), paint)
        }
    }

    private fun drawVectorPaths(
        canvas: Canvas,
        paths: List<DrawPath>,
        scaleX: Float,
        scaleY: Float
    ) {
        val avgScale = (scaleX + scaleY) / 2f

        for (drawPath in paths) {
            if (drawPath.points.size < 2) continue

            val androidPath = Path().apply {
                val p0 = drawPath.points.first()
                moveTo(p0.x * scaleX, p0.y * scaleY)
                for (i in 1 until drawPath.points.size) {
                    val prev = drawPath.points[i - 1]
                    val curr = drawPath.points[i]
                    val midX = ((prev.x + curr.x) / 2f) * scaleX
                    val midY = ((prev.y + curr.y) / 2f) * scaleY
                    quadTo(prev.x * scaleX, prev.y * scaleY, midX, midY)
                }
                val last = drawPath.points.last()
                lineTo(last.x * scaleX, last.y * scaleY)
            }

            val strokeWidth = drawPath.strokeWidth * avgScale

            when (drawPath.brushStyle) {
                BrushStyle.PEN -> {
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = drawPath.color.toArgb()
                        style = Paint.Style.STROKE
                        this.strokeWidth = strokeWidth
                        strokeCap = Paint.Cap.ROUND
                        strokeJoin = Paint.Join.ROUND
                    }
                    canvas.drawPath(androidPath, paint)
                }
                BrushStyle.HIGHLIGHTER -> {
                    val colorWithAlpha = drawPath.color.copy(alpha = 0.45f).toArgb()
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = colorWithAlpha
                        style = Paint.Style.STROKE
                        this.strokeWidth = strokeWidth * 2.2f
                        strokeCap = Paint.Cap.SQUARE
                        strokeJoin = Paint.Join.MITER
                    }
                    canvas.drawPath(androidPath, paint)
                }
                BrushStyle.NEON_GLOW -> {
                    // Outer glow
                    val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = drawPath.color.copy(alpha = 0.35f).toArgb()
                        style = Paint.Style.STROKE
                        this.strokeWidth = strokeWidth * 3.2f
                        strokeCap = Paint.Cap.ROUND
                        strokeJoin = Paint.Join.ROUND
                    }
                    canvas.drawPath(androidPath, glowPaint)

                    // Core line
                    val corePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.WHITE
                        style = Paint.Style.STROKE
                        this.strokeWidth = strokeWidth * 0.7f
                        strokeCap = Paint.Cap.ROUND
                        strokeJoin = Paint.Join.ROUND
                    }
                    canvas.drawPath(androidPath, corePaint)
                }
                BrushStyle.ERASER -> {
                    val eraserPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.parseColor("#0E0F14")
                        style = Paint.Style.STROKE
                        this.strokeWidth = strokeWidth * 3.5f
                        strokeCap = Paint.Cap.ROUND
                        strokeJoin = Paint.Join.ROUND
                    }
                    canvas.drawPath(androidPath, eraserPaint)
                }
            }
        }
    }

    private fun drawStickers(
        canvas: Canvas,
        stickers: List<CanvasSticker>,
        scaleX: Float,
        scaleY: Float
    ) {
        val avgScale = (scaleX + scaleY) / 2f

        for (sticker in stickers) {
            canvas.save()

            // Translate to sticker center
            val posX = sticker.offset.x * scaleX
            val posY = sticker.offset.y * scaleY
            canvas.translate(posX, posY)
            canvas.rotate(sticker.rotation)
            canvas.scale(sticker.scale * avgScale, sticker.scale * avgScale)

            val def = sticker.definition
            val isStamp = def.shape == StickerShape.STAMP

            val width = if (isStamp) 140f else 220f
            val height = if (isStamp) 130f else 64f
            val corner = if (isStamp) 24f else 32f

            val rect = RectF(-width / 2f, -height / 2f, width / 2f, height / 2f)

            // Sticker background
            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = def.backgroundColor.toArgb()
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(rect, corner, corner, bgPaint)

            // Sticker border
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = def.borderColor.toArgb()
                style = Paint.Style.STROKE
                strokeWidth = 3f
            }
            canvas.drawRoundRect(rect, corner, corner, borderPaint)

            // Emoji & Text
            if (isStamp) {
                val emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 48f
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText(def.iconEmoji, 0f, -4f, emojiPaint)

                val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = def.textColor.toArgb()
                    textSize = 18f
                    typeface = Typeface.DEFAULT_BOLD
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText(def.title, 0f, height / 2f - 16f, labelPaint)
            } else {
                val emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 28f
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText(def.iconEmoji, -width / 2f + 32f, 10f, emojiPaint)

                val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = def.textColor.toArgb()
                    textSize = 20f
                    typeface = Typeface.DEFAULT_BOLD
                    textAlign = Paint.Align.LEFT
                }
                val hasSubtitle = !def.subtitle.isNullOrBlank()
                val textY = if (hasSubtitle) -2f else 8f
                canvas.drawText(def.title, -width / 2f + 62f, textY, textPaint)

                if (hasSubtitle) {
                    val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = def.textColor.copy(alpha = 0.8f).toArgb()
                        textSize = 14f
                        typeface = Typeface.DEFAULT
                        textAlign = Paint.Align.LEFT
                    }
                    canvas.drawText(def.subtitle!!, -width / 2f + 62f, 20f, subPaint)
                }
            }

            canvas.restore()
        }
    }

    private fun drawCaption(canvas: Canvas, caption: StoryCaption) {
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = caption.textColor.toArgb()
            textSize = 42f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }

        val textWidth = textPaint.measureText(caption.text)
        val textHeight = 44f
        val paddingH = 48f
        val paddingV = 24f

        val pillWidth = textWidth + paddingH * 2
        val pillHeight = textHeight + paddingV * 2
        val centerX = STORY_WIDTH / 2f
        val centerY = STORY_HEIGHT * 0.85f

        val rect = RectF(
            centerX - pillWidth / 2f,
            centerY - pillHeight / 2f,
            centerX + pillWidth / 2f,
            centerY + pillHeight / 2f
        )

        when (caption.style) {
            CaptionStyle.ROUNDED_PILL -> {
                val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.parseColor("#CC121316")
                    style = Paint.Style.FILL
                }
                canvas.drawRoundRect(rect, 40f, 40f, bgPaint)

                val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.parseColor("#44FFFFFF")
                    style = Paint.Style.STROKE
                    strokeWidth = 3f
                }
                canvas.drawRoundRect(rect, 40f, 40f, borderPaint)
            }
            CaptionStyle.SOLID_BLOCK -> {
                val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.BLACK
                    style = Paint.Style.FILL
                }
                canvas.drawRect(rect, bgPaint)
            }
            CaptionStyle.OUTLINE -> {
                val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = caption.textColor.toArgb()
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                }
                canvas.drawRoundRect(rect, 16f, 16f, borderPaint)
            }
            CaptionStyle.TRANSPARENT -> {
                // No background box
            }
        }

        // Draw caption text centered
        canvas.drawText(caption.text, centerX, centerY + textHeight / 3f, textPaint)
    }
}
