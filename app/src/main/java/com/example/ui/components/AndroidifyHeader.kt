package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import com.example.ui.theme.AndroidifyBlack
import com.example.ui.theme.AndroidifySurface

@Composable
fun AndroidifyHeader(
    title: String = "StorStudio",
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showInfoDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("androidify_header"),
        shape = RoundedCornerShape(32.dp),
        color = AndroidifySurface,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFE2EAE2)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Action / Back button
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("header_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AndroidifyBlack,
                        modifier = Modifier.size(22.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Title in center of app bar
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = AndroidifyBlack,
                letterSpacing = (-0.3).sp
            )

            // Info (i) icon in circle
            IconButton(
                onClick = { showInfoDialog = true },
                modifier = Modifier.testTag("header_info_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .border(1.2.dp, AndroidifyBlack, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "i",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AndroidifyBlack
                    )
                }
            }
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(
                    text = "StorStudio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AndroidifyBlack
                )
            },
            text = {
                Text(
                    text = "Create creative status stories with drawing brushes and Figma Project Status Badges!\n\n• Capture or pick any photo\n• Slap on project status & mood stickers\n• Draw freely with glowing and highlighter pens\n• Add custom captions\n• Share directly to Instagram Stories without duplicate images",
                    fontSize = 14.sp,
                    color = Color(0xFF454B46),
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AndroidifyBlack,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Got it")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
