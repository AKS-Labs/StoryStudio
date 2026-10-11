package com.example.stickers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StickerCategory
import com.example.model.StickerDefinition
import com.example.model.StickerRepository
import com.example.ui.theme.AndroidifyBlack
import com.example.ui.theme.AndroidifyGreen
import com.example.ui.theme.AndroidifySurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickerSheet(
    onStickerSelected: (StickerDefinition) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val categories = listOf(
        Triple("Project Status", StickerCategory.PROJECT_STATUS, Icons.Default.PendingActions),
        Triple("Mood & Goals", StickerCategory.MOOD_AND_GOALS, Icons.Default.EmojiEmotions),
        Triple("Reactions", StickerCategory.REACTIONS, Icons.Default.LocalFireDepartment)
    )

    val currentStickers = when (selectedTabIndex) {
        0 -> StickerRepository.projectStatusStickers
        1 -> StickerRepository.moodAndGoalsStickers
        else -> StickerRepository.reactionStickers
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AndroidifySurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 44.dp, height = 5.dp)
                    .background(Color(0xFFD0D7D0), CircleShape)
            )
        },
        modifier = modifier.testTag("sticker_drawer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sticker Library",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AndroidifyBlack
                    )
                    Text(
                        text = "Figma Project Status Badges & Stamps",
                        fontSize = 12.sp,
                        color = Color(0xFF6B726C)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_stickers_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close sticker drawer",
                        tint = AndroidifyBlack
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Category Tab Row
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = AndroidifySurface,
                contentColor = AndroidifyBlack,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                        color = AndroidifyGreen,
                        width = 48.dp
                    )
                }
            ) {
                categories.forEachIndexed { index, item ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = item.first,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTabIndex == index) AndroidifyBlack else Color(0xFF888F88),
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = item.third,
                                contentDescription = item.first,
                                tint = if (selectedTabIndex == index) AndroidifyGreen else Color(0xFF888F88)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Grid of stickers
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                items(currentStickers, key = { it.id }) { sticker ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onStickerSelected(sticker)
                            }
                            .testTag("sticker_item_${sticker.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        StickerBadgeView(
                            sticker = sticker,
                            isSelected = false
                        )
                    }
                }
            }
        }
    }
}
