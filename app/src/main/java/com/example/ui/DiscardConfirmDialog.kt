package com.example.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun DiscardConfirmDialog(
    onConfirmDiscard: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = Color(0xFFFF5252)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Discard Story Edits?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AndroidifyBlack
                )
            }
        },
        text = {
            Text(
                text = "If you discard now, your drawing strokes, status stickers, and caption will not be saved.",
                fontSize = 14.sp,
                color = Color(0xFF5A605B)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDiscard,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF3B30),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("confirm_discard_button")
            ) {
                Text("Discard", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, AndroidifyBlack),
                modifier = Modifier.testTag("cancel_discard_button")
            ) {
                Text("Keep Editing", color = AndroidifyBlack)
            }
        },
        containerColor = AndroidifySurface,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.testTag("discard_confirm_dialog")
    )
}
