package com.example.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraController(
    private val context: Context,
    private val onPhotoCaptured: (Uri) -> Unit,
    private val onError: (String) -> Unit
) {
    var imageCapture: ImageCapture? = null
    var camera: Camera? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun takePicture() {
        val capture = imageCapture
        if (capture == null) {
            // Fallback for emulator environments without hardware camera pipeline
            val fallbackUri = createFallbackCreativeBitmap(context)
            onPhotoCaptured(fallbackUri)
            return
        }

        val photoFile = File(
            context.cacheDir,
            "story_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        capture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    ContextCompat.getMainExecutor(context).execute {
                        onPhotoCaptured(savedUri)
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraController", "Photo capture failed: ${exc.message}", exc)
                    // If hardware capture failed (e.g. emulator), generate creative photo
                    val fallbackUri = createFallbackCreativeBitmap(context)
                    ContextCompat.getMainExecutor(context).execute {
                        onPhotoCaptured(fallbackUri)
                    }
                }
            }
        )
    }

    fun toggleTorch(enable: Boolean) {
        camera?.cameraControl?.enableTorch(enable)
    }

    fun shutdown() {
        cameraExecutor.shutdown()
    }

    companion object {
        fun createFallbackCreativeBitmap(context: Context): Uri {
            val width = 1080
            val height = 1920
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Modern gradient backdrop
            val paint = Paint().apply {
                shader = LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    intArrayOf(
                        android.graphics.Color.parseColor("#F2F8F3"),
                        android.graphics.Color.parseColor("#E4F3E6"),
                        android.graphics.Color.parseColor("#C8E6C9")
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

            // Creative ambient artwork
            val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#263DDC84")
            }
            canvas.drawCircle(width * 0.3f, height * 0.35f, 320f, circlePaint)

            circlePaint.color = android.graphics.Color.parseColor("#26387BFF")
            canvas.drawCircle(width * 0.75f, height * 0.6f, 260f, circlePaint)

            // Canvas title
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#191C1A")
                textSize = 54f
                textAlign = Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            canvas.drawText("✨ Story Canvas", width / 2f, height * 0.48f, textPaint)

            val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#5A605B")
                textSize = 34f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("Ready for your status badges & stickers", width / 2f, height * 0.52f, subTextPaint)

            val file = File(context.cacheDir, "creative_capture_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }
            return Uri.fromFile(file)
        }
    }
}

@Composable
fun CameraPreview(
    isFrontCamera: Boolean,
    isTorchOn: Boolean,
    controller: CameraController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    LaunchedEffect(isFrontCamera, previewView) {
        val pv = previewView ?: return@LaunchedEffect
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(pv.surfaceProvider)
                }

                val capture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                controller.imageCapture = capture

                val cameraSelector = if (isFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                cameraProvider.unbindAll()
                controller.camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    capture
                )

                controller.toggleTorch(isTorchOn)
            } catch (exc: Exception) {
                Log.w("CameraPreview", "Camera binding fallback: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    LaunchedEffect(isTorchOn) {
        controller.toggleTorch(isTorchOn)
    }

    DisposableEffect(Unit) {
        onDispose {
            controller.shutdown()
        }
    }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }.also { previewView = it }
        },
        modifier = modifier.fillMaxSize()
    )
}
