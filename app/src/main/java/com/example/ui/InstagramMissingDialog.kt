package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.export.InstagramShareHelper
import com.example.ui.theme.AndroidifyBlack
import com.example.ui.theme.AndroidifyBlue
import com.example.ui.theme.AndroidifyGreen
import com.example.ui.theme.AndroidifySurface

@Composable
fun InstagramMissingDialog(
    storyUri: Uri?,
    caption: String?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = AndroidifyGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Instagram App Needed",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AndroidifyBlack
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "The Instagram app is required to launch Instagram Stories directly.",
                    fontSize = 14.sp,
                    color = Color(0xFF424742)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You can install Instagram from Google Play, or share your created story to another app right now.",
                    fontSize = 13.sp,
                    color = Color(0xFF5A605B)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    InstagramShareHelper.openPlayStoreForInstagram(context)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AndroidifyBlack,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("install_instagram_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shop,
                        contentDescription = null,
                        modifier = Modifier.width(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Get Instagram", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (storyUri != null) {
                OutlinedButton(
                    onClick = {
                        shareFallback(context, storyUri, caption)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, AndroidifyBlack),
                    modifier = Modifier.testTag("share_fallback_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.width(14.dp),
                            tint = AndroidifyBlack
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share via Other Apps", color = AndroidifyBlack)
                    }
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = AndroidifyBlack)
                }
            }
        },
        containerColor = AndroidifySurface,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.testTag("instagram_missing_dialog")
    )
}

private fun shareFallback(context: Context, storyUri: Uri, caption: String?) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, storyUri)
        if (!caption.isNullOrBlank()) {
            putExtra(Intent.EXTRA_TEXT, caption)
        }
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(Intent.createChooser(intent, "Share Story to..."))
}
