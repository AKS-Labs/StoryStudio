package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.StickerCategory
import com.example.model.StickerRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `verify app name resource is StoryCanvas`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("StorStudio", appName)
  }

  @Test
  fun `verify project status stickers library contains required status badges`() {
    val stickers = StickerRepository.projectStatusStickers
    assertTrue(stickers.isNotEmpty())

    val inProgress = stickers.find { it.id == "status_in_progress" }
    assertNotNull(inProgress)
    assertEquals(StickerCategory.PROJECT_STATUS, inProgress?.category)

    val done = stickers.find { it.id == "status_done" }
    assertNotNull(done)

    val needReview = stickers.find { it.id == "status_need_review" }
    assertNotNull(needReview)

    val blocked = stickers.find { it.id == "status_blocked" }
    assertNotNull(blocked)
  }

  @Test
  fun `verify mood and reactions sticker packs are populated for everyday users`() {
    val moodStickers = StickerRepository.moodAndGoalsStickers
    assertTrue(moodStickers.any { it.title.contains("FOCUS") })
    assertTrue(moodStickers.any { it.title.contains("COFFEE") })

    val reactions = StickerRepository.reactionStickers
    assertTrue(reactions.any { it.iconEmoji == "🔥" })
    assertTrue(reactions.any { it.iconEmoji == "🚀" })
  }
}
