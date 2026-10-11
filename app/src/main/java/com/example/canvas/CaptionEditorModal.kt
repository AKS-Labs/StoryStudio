package com.example.canvas

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.CaptionStyle
import com.example.model.StoryCaption
import com.example.ui.theme.AndroidifyBlack
import com.example.ui.theme.AndroidifyGreen
import com.example.ui.theme.AndroidifySurface
import com.example.ui.theme.PaletteAndroidGreen
import com.example.ui.theme.PaletteCyberYellow
import com.example.ui.theme.PaletteGoogleBlue
import com.example.ui.theme.PaletteHotPink
import com.example.ui.theme.PaletteMidnight
import com.example.ui.theme.PalettePureWhite

@Composable
fun CaptionEditorModal(
    caption: StoryCaption,
    onSaveCaption: (StoryCaption) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(caption.text) }
    var selectedColor by remember { mutableStateOf(caption.textColor) }
    var selectedStyle by remember { mutableStateOf(caption.style) }

    val presetTags = listOf("#status", "#today", "#goals", "#wip", "#vibes", "#focus", "#celebrating")
    val textColors = listOf(
        PalettePureWhite,
        PaletteMidnight,
        PaletteAndroidGreen,
        PaletteGoogleBlue,
        PaletteCyberYellow,
        PaletteHotPink
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("caption_editor_modal"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AndroidifySurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AndroidifyBlack),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Story Caption",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AndroidifyBlack
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_caption_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AndroidifyBlack
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Text Input
                OutlinedTextField(
                    value = text,
                    onValueChange = { if (it.length <= 140) text = it },
                    placeholder = {
                        Text(
                            text = "Add a caption or status update...",
                            color = Color(0xFF888F88)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("caption_input_field"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AndroidifyBlack,
                        unfocusedTextColor = AndroidifyBlack,
                        focusedBorderColor = AndroidifyBlack,
                        unfocusedBorderColor = Color(0xFFD0D7D0),
                        cursorColor = AndroidifyBlack
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick hashtag chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetTags.forEach { tag ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                text = if (text.isBlank()) tag else "$text $tag"
                            },
                            label = { Text(tag, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFFF0F5F0),
                                labelColor = AndroidifyBlack
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Style Selector (Pill vs Solid vs Clean)
                Text(
                    text = "Background Style",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5A605B)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val styles = listOf(
                        CaptionStyle.ROUNDED_PILL to "Pill",
                        CaptionStyle.SOLID_BLOCK to "Solid",
                        CaptionStyle.TRANSPARENT to "Clean"
                    )

                    styles.forEach { (style, name) ->
                        FilterChip(
                            selected = selectedStyle == style,
                            onClick = { selectedStyle = style },
                            label = { Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AndroidifyBlack,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF0F5F0),
                                labelColor = AndroidifyBlack
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Text Color Palette
                Text(
                    text = "Text Color",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5A605B)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    textColors.forEach { color ->
                        val isSelected = selectedColor == color
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) AndroidifyBlack else Color(0xFFD0D7D0),
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (caption.text.isNotBlank()) {
                        IconButton(
                            onClick = {
                                onSaveCaption(caption.copy(text = "", isVisible = false))
                                onDismiss()
                            },
                            modifier = Modifier.testTag("remove_caption_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove caption",
                                tint = Color(0xFFFF5252)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onSaveCaption(
                                caption.copy(
                                    text = text.trim(),
                                    textColor = selectedColor,
                                    style = selectedStyle,
                                    isVisible = text.isNotBlank()
                                )
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AndroidifyBlack,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("save_caption_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Apply Caption", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
