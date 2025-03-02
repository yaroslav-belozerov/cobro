package com.yaabelozerov.tribede.ui.components

import android.graphics.Bitmap
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.yaabelozerov.tribede.domain.QrCodeAnalizer

@Composable
fun ScanQR(modifier: Modifier, hasCameraPermission: Boolean, onScan: (String) -> Unit) {

  val context = LocalContext.current
  val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
  val lifeCycleOwner = LocalLifecycleOwner.current
  val selector1 =
      ResolutionSelector.Builder()
          .setResolutionStrategy(
              ResolutionStrategy(Size(1280, 720), ResolutionStrategy.FALLBACK_RULE_NONE))
          .build()
  Column(modifier) {
    if (hasCameraPermission) {
      AndroidView(
          factory = { context ->
            val previewView = PreviewView(context)
            val preview = Preview.Builder().build()
            val selector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            val imageAnalysis =
                ImageAnalysis.Builder()
                    .setResolutionSelector(selector1)
                    .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                    .build()
            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(context), QrCodeAnalizer { result -> onScan(result) })
            try {
              cameraProviderFuture
                  .get()
                  .bindToLifecycle(lifeCycleOwner, selector, preview, imageAnalysis)
            } catch (e: Exception) {
              e.printStackTrace()
            }
            previewView
          })
    }
  }
}

fun generateQRCode(data: String): Bitmap? {
  return try {
    val barcodeEncoder = BarcodeEncoder()
    barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400)
  } catch (e: Exception) {
    e.printStackTrace()
    null
  }
}
