package com.yaabelozerov.tribede.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun SimpleCameraScreen() {
    val context = LocalContext.current

    // Состояние для хранения URI фотографии
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Создаем временный файл для фотографии
    val photoFile = remember {
        File.createTempFile(
            "photo_", // Префикс имени файла
            ".jpg",   // Расширение файла
            context.getExternalFilesDir("Pictures") // Папка для сохранения
        )
    }

    // URI для файла
    val photoUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }

    // Launcher для запуска камеры
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            imageUri = photoUri
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        // Кнопка для запуска камеры
        Button(onClick = { cameraLauncher.launch(photoUri) }) {
            Text("Сделать фото")
        }

        // Отображение фотографии
        if (imageUri != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Фотография",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}