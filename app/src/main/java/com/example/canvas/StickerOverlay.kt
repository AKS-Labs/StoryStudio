package com.example.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.CanvasSticker
import com.example.stickers.StickerBadgeView
import com.example.ui.theme.AndroidifyBlack
import com.example.ui.theme.AndroidifyBlue
import kotlin.math.roundToInt

@Composable
fun StickerOverlay(
    stickers: List<CanvasSticker>,
    selectedStickerId: String?,
    isDrawingActive: Boolean,
    onSelectSticker: (String?) -> Unit,
    onTransformDelta: (stickerId: String, pan: Offset, zoom: Float, rotationChange: Float) -> Unit,
    onDeleteSticker: (String) -> Unit,
    onDuplicateSticker: (CanvasSticker) -> Unit,
    modifier: Modifier = Modifier
) {
    val updatedOnTransformDelta by rememberUpdatedState(onTransformDelta)
    val updatedOnSelectSticker by rememberUpdatedState(onSelectSticker)

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (!isDrawingActive) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures {
                            updatedOnSelectSticker(null)
                        }
                    }
                } else Modifier
            )
    ) {
        stickers.forEach { sticker ->
            val isSelected = sticker.id == selectedStickerId

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            sticker.offset.x.roundToInt(),
                            sticker.offset.y.roundToInt()
                        )
                    }
                    .rotate(sticker.rotation)
                    .scale(sticker.scale)
                    .then(
                        if (!isDrawingActive) {
                            Modifier
                                .pointerInput(sticker.id) {
                                    detectTransformGestures(
                                        panZoomLock = false,
                                        onGesture = { _, pan, zoom, rotationChange ->
                                            updatedOnSelectSticker(sticker.id)
                                            updatedOnTransformDelta(
                                                sticker.id,
                                                pan,
                                                zoom,
                                                rotationChange
                                            )
                                        }
                                    )
                                }
                        } else Modifier
                    )
                    .testTag("canvas_sticker_${sticker.id}")
            ) {
                // Sticker container with selection boundary
                Box(
                    modifier = Modifier
                        .padding(14.dp)
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = AndroidifyBlack,
                                    shape = RoundedCornerShape(22.dp)
                                )
                            } else Modifier
                        )
                        .clickable(enabled = !isDrawingActive) {
                            updatedOnSelectSticker(if (isSelected) null else sticker.id)
                        }
                ) {
                    StickerBadgeView(
                        sticker = sticker.definition,
                        isSelected = isSelected
                    )
                }

                // Interactive control handles when selected
                if (isSelected && !isDrawingActive) {
                    // Delete Knob (Top-Right)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(28.dp)
                            .background(Color(0xFFFF5252), CircleShape)
                            .border(1.5.dp, Color.White, CircleShape)
                            .clickable { onDeleteSticker(sticker.id) }
                            .testTag("delete_sticker_${sticker.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete Sticker",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Duplicate Knob (Top-Left)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .size(28.dp)
                            .background(AndroidifyBlue, CircleShape)
                            .border(1.5.dp, Color.White, CircleShape)
                            .clickable { onDuplicateSticker(sticker) }
                            .testTag("duplicate_sticker_${sticker.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Duplicate Sticker",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
