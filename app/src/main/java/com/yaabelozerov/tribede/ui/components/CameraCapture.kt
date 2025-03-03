package com.yaabelozerov.tribede.ui.components

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Composable
fun CameraCapture(
    onImageCaptured: (Uri?) -> Unit,
    lifecycleOwner: LifecycleOwner
) {
    val context = LocalContext.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            imageCapture = imageCapture,
            lifecycleOwner = lifecycleOwner
        )

        MyButton(
            onClick = {
                takePhoto(
                    context = context,
                    imageCapture = imageCapture,
                    executor = cameraExecutor,
                    onImageCaptured = onImageCaptured
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp), text = "Сделать фото"
        )
    }
}

@Composable
fun CameraPreview(
    imageCapture: ImageCapture,
    lifecycleOwner: LifecycleOwner
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val preview = remember { Preview.Builder().build() }
    val cameraSelector = remember { CameraSelector.DEFAULT_BACK_CAMERA }

    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                preview.setSurfaceProvider(previewView.surfaceProvider)
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            }, ContextCompat.getMainExecutor(context))
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Uri?) -> Unit
) {
    val file = File.createTempFile(
        "IMG_${System.currentTimeMillis()}",
        ".jpg",
        context.externalCacheDir
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val uri = Uri.fromFile(file)
                onImageCaptured(uri)
            }

            override fun onError(exc: ImageCaptureException) {
                Log.e("Camera", "Error capturing image", exc)
                onImageCaptured(null)
            }
        }
    )
}