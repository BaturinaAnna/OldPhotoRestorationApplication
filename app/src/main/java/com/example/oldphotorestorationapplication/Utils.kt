package com.example.oldphotorestorationapplication

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
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
