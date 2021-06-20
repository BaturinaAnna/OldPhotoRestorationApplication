package com.example.oldphotorestorationapplication

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun convertToFile(bitmap: Bitmap, context: Context): File {
    val file = File(context.cacheDir, "imageToRestore")
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos)
    val bitmapData = bos.toByteArray()
    val fos = FileOutputStream(file)
    fos.write(bitmapData)
    fos.flush()
    fos.close()
    return file
}

fun Bitmap.toByteArray(): ByteArray{
    val byteArrayOutputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}



//fun compressBitmap(originalBitmap: Bitmap): Bitmap {
//    val out = ByteArrayOutputStream()
//    originalBitmap.compress(Bitmap.CompressFormat.PNG, 0, out)
//    return BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
//}

//fun resizeImage(image: Bitmap): Bitmap {
//    val width = image.width
//    val height = image.height
//
//    val scaleWidth = width / 10
//    val scaleHeight = height / 10
//
//    if (image.byteCount <= 1000000)
//        return image
//
//    return Bitmap.createScaledBitmap(image, scaleWidth, scaleHeight, false)
//}


fun showAlertDialog(message: String,
                    textPositive: String = "Yes",
                    actionsPositive: DialogInterface.OnClickListener,
                    textNegative: String = "No",
                    actionsNegative: DialogInterface.OnClickListener,
                    context: Context){
    val alertDialogBuilder = context.let { AlertDialog.Builder(it) }
    alertDialogBuilder.setPositiveButton(textPositive, actionsPositive)
    alertDialogBuilder.setNegativeButton(textNegative, actionsNegative)
    alertDialogBuilder.setMessage(message)
    alertDialogBuilder.create().show()
}
