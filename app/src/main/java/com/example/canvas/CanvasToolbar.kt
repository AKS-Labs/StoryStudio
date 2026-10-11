package com.example.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AndroidifyBlack
import com.example.ui.theme.AndroidifyGreen
import com.example.ui.theme.AndroidifySurface

@Composable
fun CanvasToolbar(
    isDrawingActive: Boolean,
    brushColor: Color,
    canUndo: Boolean,
    canRedo: Boolean,
    onToggleDraw: () -> Unit,
    onOpenStickers: () -> Unit,
    onOpenCaption: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("canvas_bottom_toolbar"),
        shape = RoundedCornerShape(32.dp),
        color = AndroidifySurface,
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFE0E5E0))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Draw Button with Brush Color Badge
            IconButton(
                onClick = onToggleDraw,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isDrawingActive) AndroidifyBlack else Color.Transparent,
                    contentColor = if (isDrawingActive) Color.White else AndroidifyBlack
                ),
                modifier = Modifier
                    .size(46.dp)
                    .testTag("toggle_draw_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Draw,
                        contentDescription = "Draw mode",
                        modifier = Modifier.size(24.dp)
                    )
                    // Indicator dot for active color
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(10.dp)
                            .background(brushColor, CircleShape)
                            .border(1.dp, Color.White, CircleShape)
                    )
                }
            }

            // Stickers Button
            IconButton(
                onClick = onOpenStickers,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = AndroidifyBlack
                ),
                modifier = Modifier
                    .size(46.dp)
                    .testTag("open_stickers_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Loyalty,
                    contentDescription = "Sticker library",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Caption Button
            IconButton(
                onClick = onOpenCaption,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = AndroidifyBlack
                ),
                modifier = Modifier
                    .size(46.dp)
                    .testTag("open_caption_button")
            ) {
                Icon(
                    imageVector = Icons.Default.TextFields,
                    contentDescription = "Add caption",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .size(width = 1.dp, height = 24.dp)
                    .background(Color(0xFFD0D7D0))
            )
            Spacer(modifier = Modifier.width(2.dp))

            // Undo Button (Fully functional with history manager)
            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = AndroidifyBlack,
                    disabledContentColor = Color(0xFFC0C7C0)
                ),
                modifier = Modifier
                    .size(44.dp)
                    .testTag("undo_action_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    modifier = Modifier.size(22.dp)
                )
            }

            // Redo Button (Fully functional with history manager)
            IconButton(
                onClick = onRedo,
                enabled = canRedo,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = AndroidifyBlack,
                    disabledContentColor = Color(0xFFC0C7C0)
                ),
                modifier = Modifier
                    .size(44.dp)
                    .testTag("redo_action_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Redo",
                    modifier = Modifier.size(22.dp)
                )
            }

            // Clear All Button
            IconButton(
                onClick = onClearAll,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color(0xFFFF5252)
                ),
                modifier = Modifier
                    .size(44.dp)
                    .testTag("clear_all_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear all",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
