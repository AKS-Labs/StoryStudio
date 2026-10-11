package com.example.export

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

sealed class ShareResult {
    data object Success : ShareResult()
    data object InstagramNotInstalled : ShareResult()
    data class Error(val message: String) : ShareResult()
}

object InstagramShareHelper {

    private const val INSTAGRAM_PACKAGE_NAME = "com.instagram.android"
    private const val ACTION_ADD_TO_STORY = "com.instagram.share.ADD_TO_STORY"

    fun isInstagramInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(INSTAGRAM_PACKAGE_NAME, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Directly launches Instagram Stories with the single rendered composite image
     * without duplicating assets (no interactive_asset_uri duplication).
     */
    fun shareToInstagramStories(
        context: Context,
        storyUri: Uri,
        captionText: String?
    ): ShareResult {
        // Copy caption to clipboard so user can also paste it into Instagram text tool if desired
        if (!captionText.isNullOrBlank()) {
            copyToClipboard(context, captionText)
        }

        if (!isInstagramInstalled(context)) {
            return ShareResult.InstagramNotInstalled
        }

        return try {
            val storiesIntent = Intent(ACTION_ADD_TO_STORY).apply {
                // Pass single background asset so Instagram doesn't show two identical images
                setDataAndType(storyUri, "image/png")
                setPackage(INSTAGRAM_PACKAGE_NAME)
                putExtra("top_background_color", "#F2F8F3")
                putExtra("bottom_background_color", "#191C1A")
                putExtra("source_application", context.packageName)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(storiesIntent)

            val toastMsg = if (!captionText.isNullOrBlank()) {
                "Opening Instagram Story! Caption embedded and copied to clipboard 📋"
            } else {
                "Opening Instagram Story! 🚀"
            }
            Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show()

            ShareResult.Success
        } catch (e: Exception) {
            ShareResult.Error(e.localizedMessage ?: "Failed to launch Instagram Stories")
        }
    }

    fun openPlayStoreForInstagram(context: Context) {
        try {
            val marketIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$INSTAGRAM_PACKAGE_NAME")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(marketIntent)
        } catch (e: Exception) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$INSTAGRAM_PACKAGE_NAME")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    private fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("Story Caption", text)
        clipboard?.setPrimaryClip(clip)
    }
}
