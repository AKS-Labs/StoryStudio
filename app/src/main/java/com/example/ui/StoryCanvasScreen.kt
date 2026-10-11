package com.example.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.canvas.BrushControlBar
import com.example.canvas.CanvasAction
import com.example.canvas.CanvasHistoryManager
import com.example.canvas.CanvasToolbar
import com.example.canvas.CaptionEditorModal
import com.example.canvas.DrawingCanvas
import com.example.canvas.StickerOverlay
import com.example.export.InstagramShareHelper
import com.example.export.ShareResult
import com.example.export.StoryExporter
import com.example.model.BrushStyle
import com.example.model.CaptionStyle
import com.example.model.CanvasSticker
import com.example.model.DrawPath
import com.example.model.StoryCaption
import com.example.stickers.StickerSheet
import com.example.ui.theme.AndroidifyBlack
import com.example.ui.theme.AndroidifyGreen
import com.example.ui.theme.AndroidifyPaleMint
import com.example.ui.theme.AndroidifySurface
import com.example.ui.theme.PaletteAndroidGreen
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun StoryCanvasScreen(
    photoUri: Uri?,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Core Canvas State
    var paths by remember { mutableStateOf<List<DrawPath>>(emptyList()) }
    var stickers by remember { mutableStateOf<List<CanvasSticker>>(emptyList()) }
    var caption by remember { mutableStateOf(StoryCaption()) }

    // History Manager for Rock-solid Undo and Redo
    val historyManager = remember {
        CanvasHistoryManager { restoredPaths, restoredStickers, restoredCaption ->
            paths = restoredPaths
            stickers = restoredStickers
            caption = restoredCaption
        }
    }

    // Tools & Selection State
    var isDrawingActive by remember { mutableStateOf(false) }
    var currentBrushStyle by remember { mutableStateOf(BrushStyle.PEN) }
    var currentBrushColor by remember { mutableStateOf(PaletteAndroidGreen) }
    var currentStrokeWidth by remember { mutableFloatStateOf(10f) }

    var selectedStickerId by remember { mutableStateOf<String?>(null) }
    var showStickerSheet by remember { mutableStateOf(false) }
    var showCaptionModal by remember { mutableStateOf(false) }

    // Dialog & Export States
    var showDiscardDialog by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }
    var showInstagramMissingDialog by remember { mutableStateOf(false) }
    var exportedStoryUri by remember { mutableStateOf<Uri?>(null) }

    BackHandler {
        if (paths.isNotEmpty() || stickers.isNotEmpty() || caption.text.isNotBlank()) {
            showDiscardDialog = true
        } else {
            onDiscard()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AndroidifyPaleMint)
    ) {
        // Center Canvas Frame with Dashed Androidify Outline
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 74.dp, bottom = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(9f / 16f)
                    .drawBehind {
                        val stroke = Stroke(
                            width = 3.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 16f), 0f)
                        )
                        drawRoundRect(
                            color = AndroidifyBlack,
                            cornerRadius = CornerRadius(28.dp.toPx(), 28.dp.toPx()),
                            style = stroke
                        )
                    }
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .testTag("story_canvas_surface")
            ) {
                // 1. Photo Background Layer
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Base Story Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFE8F5E9),
                                        Color(0xFFC8E6C9),
                                        Color(0xFFA5D6A7)
                                    )
                                )
                            )
                    )
                }

                // 2. Freehand Drawing Layer
                DrawingCanvas(
                    isDrawingActive = isDrawingActive,
                    currentColor = currentBrushColor,
                    currentStrokeWidth = currentStrokeWidth,
                    currentBrushStyle = currentBrushStyle,
                    paths = paths,
                    onPathCompleted = { newStroke ->
                        paths = paths + newStroke
                        historyManager.recordAction(CanvasAction.AddStroke(newStroke))
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // 3. Interactive Transformable Stickers Layer
                StickerOverlay(
                    stickers = stickers,
                    selectedStickerId = selectedStickerId,
                    isDrawingActive = isDrawingActive,
                    onSelectSticker = { selectedStickerId = it },
                    onTransformDelta = { stickerId, pan, zoom, rotationChange ->
                        stickers = stickers.map { current ->
                            if (current.id == stickerId) {
                                current.copy(
                                    offset = current.offset + pan,
                                    scale = (current.scale * zoom).coerceIn(0.4f, 3.5f),
                                    rotation = (current.rotation + rotationChange) % 360f
                                )
                            } else current
                        }
                    },
                    onDeleteSticker = { idToDelete ->
                        val toDelete = stickers.find { it.id == idToDelete }
                        if (toDelete != null) {
                            stickers = stickers.filter { it.id != idToDelete }
                            if (selectedStickerId == idToDelete) selectedStickerId = null
                            historyManager.recordAction(CanvasAction.DeleteSticker(toDelete))
                        }
                    },
                    onDuplicateSticker = { original ->
                        val duplicate = original.copy(
                            id = UUID.randomUUID().toString(),
                            offset = original.offset + Offset(30f, 30f),
                            isSelected = true
                        )
                        stickers = stickers + duplicate
                        selectedStickerId = duplicate.id
                        historyManager.recordAction(CanvasAction.AddSticker(duplicate))
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // 4. Live Caption Banner Preview
                if (caption.isVisible && caption.text.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                            .clickable { showCaptionModal = true }
                            .testTag("story_caption_banner"),
                        contentAlignment = Alignment.Center
                    ) {
                        when (caption.style) {
                            CaptionStyle.ROUNDED_PILL -> {
                                Box(
                                    modifier = Modifier
                                        .background(AndroidifyBlack.copy(alpha = 0.88f), RoundedCornerShape(32.dp))
                                        .border(1.2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(32.dp))
                                        .padding(horizontal = 22.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = caption.text,
                                        color = caption.textColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            CaptionStyle.SOLID_BLOCK -> {
                                Box(
                                    modifier = Modifier
                                        .background(AndroidifyBlack, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 18.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = caption.text,
                                        color = caption.textColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            CaptionStyle.OUTLINE -> {
                                Box(
                                    modifier = Modifier
                                        .border(2.dp, caption.textColor, RoundedCornerShape(14.dp))
                                        .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                                        .padding(horizontal = 18.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = caption.text,
                                        color = caption.textColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            CaptionStyle.TRANSPARENT -> {
                                Text(
                                    text = caption.text,
                                    color = caption.textColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Top Navigation & Action Header (Matching Androidify Style)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(32.dp),
            color = AndroidifySurface,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFE0E5E0)),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back / Discard Button
                IconButton(
                    onClick = {
                        if (paths.isNotEmpty() || stickers.isNotEmpty() || caption.text.isNotBlank()) {
                            showDiscardDialog = true
                        } else {
                            onDiscard()
                        }
                    },
                    modifier = Modifier.testTag("discard_edits_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AndroidifyBlack,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = "StorStudio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = AndroidifyBlack
                )

                // Share to Instagram Stories Pill Button
                Button(
                    onClick = {
                        if (isExporting) return@Button
                        isExporting = true
                        selectedStickerId = null

                        coroutineScope.launch {
                            val result = StoryExporter.exportStoryComposite(
                                context = context,
                                basePhotoUri = photoUri,
                                paths = paths,
                                stickers = stickers,
                                caption = caption,
                                viewWidth = 1080f,
                                viewHeight = 1920f
                            )

                            isExporting = false

                            result.onSuccess { storyUri ->
                                exportedStoryUri = storyUri
                                val shareResult = InstagramShareHelper.shareToInstagramStories(
                                    context = context,
                                    storyUri = storyUri,
                                    captionText = caption.text.ifBlank { null }
                                )

                                when (shareResult) {
                                    is ShareResult.Success -> {
                                        // Story opened in Instagram
                                    }
                                    is ShareResult.InstagramNotInstalled -> {
                                        showInstagramMissingDialog = true
                                    }
                                    is ShareResult.Error -> {
                                        Toast.makeText(
                                            context,
                                            "Share error: ${shareResult.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }.onFailure { err ->
                                Toast.makeText(
                                    context,
                                    "Render error: ${err.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    enabled = !isExporting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AndroidifyBlack,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("share_instagram_button")
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Exporting...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Share Story", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Controls Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brush Control Bar (Visible when Draw tool is active)
            BrushControlBar(
                isVisible = isDrawingActive,
                currentStyle = currentBrushStyle,
                currentColor = currentBrushColor,
                currentStrokeWidth = currentStrokeWidth,
                onStyleChanged = { currentBrushStyle = it },
                onColorChanged = { currentBrushColor = it },
                onStrokeWidthChanged = { currentStrokeWidth = it }
            )

            // Canvas Toolbar with Functional Undo and Redo
            CanvasToolbar(
                isDrawingActive = isDrawingActive,
                brushColor = currentBrushColor,
                canUndo = historyManager.canUndo(),
                canRedo = historyManager.canRedo(),
                onToggleDraw = {
                    isDrawingActive = !isDrawingActive
                    if (isDrawingActive) selectedStickerId = null
                },
                onOpenStickers = {
                    showStickerSheet = true
                    isDrawingActive = false
                },
                onOpenCaption = {
                    showCaptionModal = true
                },
                onUndo = {
                    historyManager.undo(paths, stickers, caption)
                },
                onRedo = {
                    historyManager.redo(paths, stickers, caption)
                },
                onClearAll = {
                    val oldPaths = paths
                    val oldStickers = stickers
                    val oldCaption = caption
                    paths = emptyList()
                    stickers = emptyList()
                    selectedStickerId = null
                    caption = StoryCaption()
                    historyManager.recordAction(CanvasAction.Clear(oldPaths, oldStickers, oldCaption))
                }
            )
        }

        // Sticker Picker Bottom Sheet with Figma Project Status Stickers
        if (showStickerSheet) {
            StickerSheet(
                onStickerSelected = { definition ->
                    val newSticker = CanvasSticker(
                        definition = definition,
                        offset = Offset(280f, 550f),
                        scale = 1.0f,
                        rotation = 0f,
                        isSelected = true
                    )
                    stickers = stickers + newSticker
                    selectedStickerId = newSticker.id
                    showStickerSheet = false
                    historyManager.recordAction(CanvasAction.AddSticker(newSticker))
                },
                onDismiss = { showStickerSheet = false }
            )
        }

        // Caption Editor Dialog
        if (showCaptionModal) {
            CaptionEditorModal(
                caption = caption,
                onSaveCaption = { updated ->
                    val old = caption
                    caption = updated
                    historyManager.recordAction(CanvasAction.UpdateCaption(old, updated))
                },
                onDismiss = { showCaptionModal = false }
            )
        }

        // Discard Confirmation Dialog
        if (showDiscardDialog) {
            DiscardConfirmDialog(
                onConfirmDiscard = {
                    showDiscardDialog = false
                    onDiscard()
                },
                onDismiss = { showDiscardDialog = false }
            )
        }

        // Instagram Missing Dialog
        if (showInstagramMissingDialog) {
            InstagramMissingDialog(
                storyUri = exportedStoryUri,
                caption = caption.text.ifBlank { null },
                onDismiss = { showInstagramMissingDialog = false }
            )
        }
    }
}
