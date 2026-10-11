package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class StickerCategory {
    PROJECT_STATUS,
    MOOD_AND_GOALS,
    REACTIONS
}

enum class StickerShape {
    PILL,
    BADGE,
    STAMP,
    TAG
}

data class StickerDefinition(
    val id: String,
    val category: StickerCategory,
    val title: String,
    val subtitle: String? = null,
    val iconEmoji: String,
    val shape: StickerShape = StickerShape.PILL,
    val backgroundColor: Color,
    val textColor: Color,
    val borderColor: Color = Color.Transparent,
    val isGlowing: Boolean = false
)

data class CanvasSticker(
    val id: String = UUID.randomUUID().toString(),
    val definition: StickerDefinition,
    val offset: Offset,
    val scale: Float = 1.0f,
    val rotation: Float = 0.0f,
    val isSelected: Boolean = false
)

object StickerRepository {
    // Figma Project Status Badges (From Figma: Project Status Stickers)
    val projectStatusStickers = listOf(
        StickerDefinition(
            id = "status_in_progress",
            category = StickerCategory.PROJECT_STATUS,
            title = "IN PROGRESS",
            subtitle = "Working on it",
            iconEmoji = "⏳",
            shape = StickerShape.PILL,
            backgroundColor = Color(0xFFFFD426),
            textColor = Color(0xFF191C1A),
            borderColor = Color(0xFFE5BE22)
        ),
        StickerDefinition(
            id = "status_done",
            category = StickerCategory.PROJECT_STATUS,
            title = "DONE",
            subtitle = "Completed",
            iconEmoji = "✅",
            shape = StickerShape.PILL,
            backgroundColor = Color(0xFF3DDC84),
            textColor = Color(0xFF003816),
            borderColor = Color(0xFF2EBD6E)
        ),
        StickerDefinition(
            id = "status_need_review",
            category = StickerCategory.PROJECT_STATUS,
            title = "NEED REVIEW",
            subtitle = "Feedback please",
            iconEmoji = "👀",
            shape = StickerShape.PILL,
            backgroundColor = Color(0xFF387BFF),
            textColor = Color(0xFFFFFFFF),
            borderColor = Color(0xFF1E5EDB)
        ),
        StickerDefinition(
            id = "status_blocked",
            category = StickerCategory.PROJECT_STATUS,
            title = "BLOCKED",
            subtitle = "Needs help",
            iconEmoji = "⛔",
            shape = StickerShape.PILL,
            backgroundColor = Color(0xFFFF5252),
            textColor = Color(0xFFFFFFFF),
            borderColor = Color(0xFFE03838)
        ),
        StickerDefinition(
            id = "status_wip",
            category = StickerCategory.PROJECT_STATUS,
            title = "W.I.P.",
            subtitle = "Draft in motion",
            iconEmoji = "🚧",
            shape = StickerShape.BADGE,
            backgroundColor = Color(0xFFFF9100),
            textColor = Color(0xFF281300),
            borderColor = Color(0xFFE07C00)
        ),
        StickerDefinition(
            id = "status_approved",
            category = StickerCategory.PROJECT_STATUS,
            title = "APPROVED",
            subtitle = "Ready to go",
            iconEmoji = "🌟",
            shape = StickerShape.PILL,
            backgroundColor = Color(0xFF00E676),
            textColor = Color(0xFF003B16),
            borderColor = Color(0xFF00C853)
        ),
        StickerDefinition(
            id = "status_on_hold",
            category = StickerCategory.PROJECT_STATUS,
            title = "ON HOLD",
            subtitle = "Paused for now",
            iconEmoji = "⏸️",
            shape = StickerShape.PILL,
            backgroundColor = Color(0xFF90A4AE),
            textColor = Color(0xFF1C272C),
            borderColor = Color(0xFF78909C)
        ),
        StickerDefinition(
            id = "status_planning",
            category = StickerCategory.PROJECT_STATUS,
            title = "PLANNING",
            subtitle = "Mapping it out",
            iconEmoji = "🗺️",
            shape = StickerShape.PILL,
            backgroundColor = Color(0xFF80D8FF),
            textColor = Color(0xFF003248),
            borderColor = Color(0xFF40C4FF)
        ),
        StickerDefinition(
            id = "status_ready",
            category = StickerCategory.PROJECT_STATUS,
            title = "READY",
            subtitle = "All set to launch",
            iconEmoji = "🚀",
            shape = StickerShape.BADGE,
            backgroundColor = Color(0xFF7C4DFF),
            textColor = Color(0xFFFFFFFF),
            borderColor = Color(0xFF651FFF),
            isGlowing = true
        )
    )

    // Creative Life & Mood Stickers for everyday users
    val moodAndGoalsStickers = listOf(
        StickerDefinition(
            id = "mood_focus",
            category = StickerCategory.MOOD_AND_GOALS,
            title = "FOCUS MODE",
            subtitle = "In the zone",
            iconEmoji = "🎯",
            shape = StickerShape.PILL,
            backgroundColor = Color(0xFF191C1A),
            textColor = Color(0xFFFFFFFF),
            borderColor = Color(0xFF3DDC84)
        ),
        StickerDefinition(
            id = "mood_coffee",
            category = StickerCategory.MOOD_AND_GOALS,
            title = "COFFEE TIME",
            subtitle = "Recharging",
            iconEmoji = "☕",
            shape = StickerShape.BADGE,
            backgroundColor = Color(0xFF795548),
            textColor = Color(0xFFFBE9E7),
            borderColor = Color(0xFF5D4037)
        ),
        StickerDefinition(
            id = "mood_idea",
            category = StickerCategory.MOOD_AND_GOALS,
            title = "BIG IDEA",
            subtitle = "Inspiration struck",
            iconEmoji = "💡",
            shape = StickerShape.BADGE,
            backgroundColor = Color(0xFFFFD426),
            textColor = Color(0xFF191C1A),
            borderColor = Color(0xFFE5BE22)
        ),
        StickerDefinition(
            id = "mood_thinking",
            category = StickerCategory.MOOD_AND_GOALS,
            title = "THINKING...",
            subtitle = "Processing thoughts",
            iconEmoji = "🤔",
            shape = StickerShape.PILL,
            backgroundColor = Color(0xFFE0E0E0),
            textColor = Color(0xFF212121),
            borderColor = Color(0xFFBDBDBD)
        ),
        StickerDefinition(
            id = "mood_celebrating",
            category = StickerCategory.MOOD_AND_GOALS,
            title = "CELEBRATING",
            subtitle = "Milestone hit!",
            iconEmoji = "🎉",
            shape = StickerShape.BADGE,
            backgroundColor = Color(0xFFFF4081),
            textColor = Color(0xFFFFFFFF),
            borderColor = Color(0xFFF50057)
        ),
        StickerDefinition(
            id = "mood_almost_done",
            category = StickerCategory.MOOD_AND_GOALS,
            title = "ALMOST DONE",
            subtitle = "Final touches",
            iconEmoji = "🏁",
            shape = StickerShape.PILL,
            backgroundColor = Color(0xFF00BFA5),
            textColor = Color(0xFFFFFFFF),
            borderColor = Color(0xFF00897B)
        )
    )

    // Expressive Stamps & Reactions
    val reactionStickers = listOf(
        StickerDefinition(
            id = "reaction_fire",
            category = StickerCategory.REACTIONS,
            title = "ON FIRE",
            iconEmoji = "🔥",
            shape = StickerShape.STAMP,
            backgroundColor = Color(0xFFFF5252),
            textColor = Color(0xFFFFFFFF)
        ),
        StickerDefinition(
            id = "reaction_hundred",
            category = StickerCategory.REACTIONS,
            title = "100%",
            iconEmoji = "💯",
            shape = StickerShape.STAMP,
            backgroundColor = Color(0xFFD50000),
            textColor = Color(0xFFFFFFFF)
        ),
        StickerDefinition(
            id = "reaction_sparkles",
            category = StickerCategory.REACTIONS,
            title = "MAGIC",
            iconEmoji = "✨",
            shape = StickerShape.STAMP,
            backgroundColor = Color(0xFFFFD426),
            textColor = Color(0xFF191C1A)
        ),
        StickerDefinition(
            id = "reaction_rocket",
            category = StickerCategory.REACTIONS,
            title = "TO THE MOON",
            iconEmoji = "🚀",
            shape = StickerShape.STAMP,
            backgroundColor = Color(0xFF387BFF),
            textColor = Color(0xFFFFFFFF)
        ),
        StickerDefinition(
            id = "reaction_love",
            category = StickerCategory.REACTIONS,
            title = "LOVE THIS",
            iconEmoji = "❤️",
            shape = StickerShape.STAMP,
            backgroundColor = Color(0xFFFF1744),
            textColor = Color(0xFFFFFFFF)
        ),
        StickerDefinition(
            id = "reaction_high_five",
            category = StickerCategory.REACTIONS,
            title = "HIGH FIVE",
            iconEmoji = "🙌",
            shape = StickerShape.STAMP,
            backgroundColor = Color(0xFF3DDC84),
            textColor = Color(0xFF003816)
        )
    )

    val allStickers: List<StickerDefinition> = projectStatusStickers + moodAndGoalsStickers + reactionStickers
}
