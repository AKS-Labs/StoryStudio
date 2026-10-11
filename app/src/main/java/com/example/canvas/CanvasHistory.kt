package com.example.canvas

import androidx.compose.ui.geometry.Offset
import com.example.model.CanvasSticker
import com.example.model.DrawPath
import com.example.model.StoryCaption

sealed class CanvasAction {
    data class AddStroke(val stroke: DrawPath) : CanvasAction()
    data class AddSticker(val sticker: CanvasSticker) : CanvasAction()
    data class DeleteSticker(val sticker: CanvasSticker) : CanvasAction()
    data class UpdateCaption(val oldCaption: StoryCaption, val newCaption: StoryCaption) : CanvasAction()
    data class Clear(
        val oldPaths: List<DrawPath>,
        val oldStickers: List<CanvasSticker>,
        val oldCaption: StoryCaption
    ) : CanvasAction()
}

class CanvasHistoryManager(
    private val onStateRestored: (
        paths: List<DrawPath>,
        stickers: List<CanvasSticker>,
        caption: StoryCaption
    ) -> Unit
) {
    val undoStack = mutableListOf<CanvasAction>()
    val redoStack = mutableListOf<CanvasAction>()

    fun recordAction(action: CanvasAction) {
        undoStack.add(action)
        redoStack.clear()
    }

    fun canUndo(): Boolean = undoStack.isNotEmpty()
    fun canRedo(): Boolean = redoStack.isNotEmpty()

    fun undo(
        currentPaths: List<DrawPath>,
        currentStickers: List<CanvasSticker>,
        currentCaption: StoryCaption
    ) {
        if (undoStack.isEmpty()) return
        val action = undoStack.removeAt(undoStack.lastIndex)
        redoStack.add(action)

        when (action) {
            is CanvasAction.AddStroke -> {
                val newPaths = if (currentPaths.isNotEmpty()) currentPaths.dropLast(1) else emptyList()
                onStateRestored(newPaths, currentStickers, currentCaption)
            }
            is CanvasAction.AddSticker -> {
                val newStickers = currentStickers.filter { it.id != action.sticker.id }
                onStateRestored(currentPaths, newStickers, currentCaption)
            }
            is CanvasAction.DeleteSticker -> {
                val newStickers = currentStickers + action.sticker
                onStateRestored(currentPaths, newStickers, currentCaption)
            }
            is CanvasAction.UpdateCaption -> {
                onStateRestored(currentPaths, currentStickers, action.oldCaption)
            }
            is CanvasAction.Clear -> {
                onStateRestored(action.oldPaths, action.oldStickers, action.oldCaption)
            }
        }
    }

    fun redo(
        currentPaths: List<DrawPath>,
        currentStickers: List<CanvasSticker>,
        currentCaption: StoryCaption
    ) {
        if (redoStack.isEmpty()) return
        val action = redoStack.removeAt(redoStack.lastIndex)
        undoStack.add(action)

        when (action) {
            is CanvasAction.AddStroke -> {
                val newPaths = currentPaths + action.stroke
                onStateRestored(newPaths, currentStickers, currentCaption)
            }
            is CanvasAction.AddSticker -> {
                val newStickers = currentStickers + action.sticker
                onStateRestored(currentPaths, newStickers, currentCaption)
            }
            is CanvasAction.DeleteSticker -> {
                val newStickers = currentStickers.filter { it.id != action.sticker.id }
                onStateRestored(currentPaths, newStickers, currentCaption)
            }
            is CanvasAction.UpdateCaption -> {
                onStateRestored(currentPaths, currentStickers, action.newCaption)
            }
            is CanvasAction.Clear -> {
                onStateRestored(emptyList(), emptyList(), StoryCaption())
            }
        }
    }
}
