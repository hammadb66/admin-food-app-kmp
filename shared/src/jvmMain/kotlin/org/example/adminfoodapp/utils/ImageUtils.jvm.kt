package org.example.adminfoodapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

@Composable
actual fun rememberBitmapFromBytes(bytes: ByteArray): ImageBitmap {
    return remember(bytes) {
        Image.makeFromEncoded(bytes).toComposeImageBitmap()
    }
}