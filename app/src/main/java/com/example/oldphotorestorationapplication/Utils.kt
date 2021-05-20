package com.example.oldphotorestorationapplication

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


fun convertToFile(bitmap: Bitmap, context: Context): File {
    val file = File(context.cacheDir, "imageToRestore")
    val bos = ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos);
    val bitmapData = bos.toByteArray();
    val fos = FileOutputStream(file);
    fos.write(bitmapData);
    fos.flush();
    fos.close();
    return file
}