package com.example.oldphotorestorationapplication

import android.Manifest
import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private var button: Button? = null
    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.buttonRestore)
        imageView = findViewById(R.id.centerimg)
        init()
    }

    private fun init() {
        button!!.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity, arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    1
                )
                Toast.makeText(applicationContext, "PERMISSION", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(applicationContext, "ALLOWED", Toast.LENGTH_SHORT).show()
                openAlbum()
            }
        }
    }

    private fun openAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, 1)
//        try {
//            handleImageOnKitKat(data)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum()
            } else {
                Toast.makeText(applicationContext, "You denied the permission", Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Build.VERSION.SDK_INT >= 19) {
                    //4.4And above
                    try {
                        if (data != null) {
                            handleImageOnKitKat(data)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    //4.4Following version
                    try {
                        if (data != null) {
                            handleImageBeforeKitKat(data)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
////            1 -> if (resultCode == MainActivity.RESULT_OK) {
//            1 -> if (resultCode == -1) {
//                //Determine the mobile phone system version number,because4.4At the beginning of the version, the selected album picture no longer returns the real Uri of the picture, but a packaged Uri,To be parsed
//                if (Build.VERSION.SDK_INT >= 19) {
//                    //4.4And above
//                    try {
//                        handleImageOnKitKat(data)
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                } else {
//                    //4.4Following version
//                    try {
//                        handleImageBeforeKitKat(data)
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//            else -> {
//            }
//        }
//    }

    //4.4And above
    @TargetApi(19)
    @Throws(IOException::class, JSONException::class)
    private fun handleImageOnKitKat(data: Intent) {
        var imagePath: String? = null
        val uri = data.data
        //If the Uri of the document type is processed by the document id
        if (DocumentsContract.isDocumentUri(this@MainActivity, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":").toTypedArray()[1] //Parse the id in digital format
                val selection = MediaStore.Images.Media._ID + "=" + id
                //Get the real picture path according to Uri and selection
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(docId)
                )
                imagePath = getImagePath(contentUri, null)
            }
            //If it is a content type Uri,Is handled in the normal way
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            imagePath = getImagePath(uri, null)
            //If it is file type Uri,Get the image path directly
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            imagePath = uri.path
        }
        displayImage(imagePath)
    }


    //4.4Following version
    @Throws(IOException::class, JSONException::class)
    private fun handleImageBeforeKitKat(data: Intent) {
        val uri = data.data
        val imagePath = getImagePath(uri, null)
        displayImage(imagePath)
    }

    //Get the real picture path according to Uri and selection
    private fun getImagePath(externalContentUri: Uri?, selection: String?): String? {
        var path: String? = null
        val cursor = this@MainActivity.contentResolver.query(
            externalContentUri!!,
            null,
            selection,
            null,
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    //Display pictures according to picture path
    private fun displayImage(imagePath: String?) {
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            imageView!!.setImageBitmap(bitmap)
            CoroutineScope(IO).launch{
                uploadPhoto(imagePath, "http://192.168.1.235:8080/OldPhotoRestoration_war_exploded/restoration-servlet")
            }
        } else {
            Toast.makeText(
                applicationContext,
                "failed to get or download image",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private val SUCCESS = 254
    private val FALL = 318

    private val mHandler: Handler = object : Handler() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                SUCCESS -> {
                    val picture_bt = msg.obj as ByteArray
                    val bitmap = BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                    imageView!!.setImageBitmap(bitmap)
                    Toast.makeText(this@MainActivity, "Request successful", Toast.LENGTH_SHORT)
                        .show()
                }
                FALL -> Toast.makeText(this@MainActivity, "Request failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    private suspend fun uploadPhoto(imagePath: String, url: String){
        withContext(Default) {

            val file: File = File(imagePath)
            var image: RequestBody? = null
            try {
                image = RequestBody.create(MediaType.parse("image/*"), file)

            } catch (e: Exception) {
                Log.d("ANNA", e.message!!)
            }
            var requestBody: RequestBody? = null
            try {
                requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", imagePath, image)
                    .build()
            } catch (e: Exception) {
                Log.d("ANNA", e.message!!)
            }
            var request: Request? = null
            try {
                request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()
            } catch (e: Exception){
                Log.d("ANNA", e.message!!)
            }
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.HOURS)
                //.writeTimeout(1, TimeUnit.HOURS)
                .connectTimeout(1, TimeUnit.HOURS)
                .build()

            val call: Call = okHttpClient.newCall(request)

            call.enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException) {
                    Log.d("ANNA", e.toString())
                    mHandler.sendEmptyMessage(318)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call?, response: Response) {
                    val buffer = ByteArrayOutputStream()
                    var nRead: Int? = null
                    val data = ByteArray(1024)
                    while (response.body()?.byteStream()?.read(data, 0, data.size)
                            .also {
                                if (it != null) {
                                    nRead = it
                                }
                            } != -1
                    ) {
                        nRead?.let { buffer.write(data, 0, it) }
                    }
                    buffer.flush()
                    val byteArray = buffer.toByteArray()
                    val message: Message = mHandler.obtainMessage()
                    message.obj = byteArray
                    message.what = 254
                    mHandler.sendMessage(message)
                }
            })
        }
    }

}