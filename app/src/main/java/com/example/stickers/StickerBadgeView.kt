package com.example.stickers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StickerDefinition
import com.example.model.StickerShape

@Composable
fun StickerBadgeView(
    sticker: StickerDefinition,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val cornerRadius = when (sticker.shape) {
        StickerShape.PILL -> 50.dp
        StickerShape.BADGE -> 16.dp
        StickerShape.STAMP -> 20.dp
        StickerShape.TAG -> 8.dp
    }

    val shape = RoundedCornerShape(cornerRadius)

    val shadowElevation = if (isSelected) 12.dp else 6.dp

    Box(
        modifier = modifier
            .shadow(shadowElevation, shape)
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        sticker.backgroundColor,
                        sticker.backgroundColor.copy(alpha = 0.92f)
                    )
                )
            )
            .border(
                width = if (isSelected) 3.dp else 1.5.dp,
                color = if (isSelected) Color.White else sticker.borderColor.copy(alpha = 0.8f),
                shape = shape
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        when (sticker.shape) {
            StickerShape.STAMP -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = sticker.iconEmoji,
                        fontSize = 32.sp
                    )
                    Text(
                        text = sticker.title,
                        color = sticker.textColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
            else -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Emoji / Icon container
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White.copy(alpha = 0.22f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sticker.iconEmoji,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = sticker.title,
                            color = sticker.textColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 0.5.sp
                        )
                        if (!sticker.subtitle.isNullOrBlank()) {
                            Text(
                                text = sticker.subtitle,
                                color = sticker.textColor.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
