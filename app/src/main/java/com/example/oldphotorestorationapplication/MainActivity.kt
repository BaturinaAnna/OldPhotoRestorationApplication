package com.example.oldphotorestorationapplication

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oldphotorestorationapplication.data.Photo
import com.example.oldphotorestorationapplication.data.PhotoViewModel
import com.example.oldphotorestorationapplication.databinding.ActivityMainBinding
import com.example.oldphotorestorationapplication.databinding.PhotoEditorBinding
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), OnPhotoClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mViewModel: PhotoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // in this method '2' represents number of columns to be displayed in grid view.
        val layoutManager = GridLayoutManager(this, 2)
        binding.photoRecyclerView.layoutManager = layoutManager

        val adapter = RecyclerViewAdapter(this)
        binding.photoRecyclerView.adapter = adapter

        mViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        mViewModel.allData.observe(this, { photo -> adapter.setData(photo) })
        init()
    }

    override fun onPhotoClick(position: Int, view: View) {
        val photo = mViewModel.allData.value?.get(position)
        val intent = Intent(view.context, PhotoEditorActivity::class.java)
        intent.putExtra("photoId", photo?.id)
        view.context.startActivity(intent)
    }

    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    private fun init() {
        binding.buttonRestore.setOnClickListener {
            //      dispatchTakePictureIntent()
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    1
                )
            } else {
                //              openAlbum()
                dispatchTakePictureIntent()
            }
        }
    }

    private fun openAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // openAlbum()
            dispatchTakePictureIntent()
        } else {
            Log.d("DebugPhotoRestorationApp", "The permission is denied")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            GlobalScope.launch {
                restorePhoto(
                    imageBitmap,
                    "http://192.168.139.113:8080/OldPhotoRestoration_war_exploded/restoration-servlet"
                )
            }
            //      mViewModel.addPhoto(Photo(imageBitmap, imageBitmap, null, null, null, null))

            //      imageView.setImageBitmap(imageBitmap)
        }
    }

    //  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    //    super.onActivityResult(requestCode, resultCode, data)
    //    try {
    //      if (data != null) {
    //        handleImageOnKitKat(data)
    //      }
    //    } catch (e: IOException) {
    //      e.printStackTrace()
    //    } catch (e: JSONException) {
    //      e.printStackTrace()
    //    }
    //  }

    @Throws(IOException::class, JSONException::class)
    private fun handleImageOnKitKat(data: Intent) {
        var imagePath: String? = null
        val uri = data.data
        // If the Uri of the document type is processed by the document id
        if (DocumentsContract.isDocumentUri(this@MainActivity, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":").toTypedArray()[1] // Parse the id in digital format
                val selection = MediaStore.Images.Media._ID + "=" + id
                // Get the real picture path according to Uri and selection
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri =
                    ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(docId)
                    )
                imagePath = getImagePath(contentUri, null)
            }
            // If it is a content type Uri,Is handled in the normal way
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            imagePath = getImagePath(uri, null)
            // If it is file type Uri,Get the image path directly
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            imagePath = uri.path
        }
        displayImage(imagePath)
    }

    // Get the real picture path according to Uri and selection
    private fun getImagePath(externalContentUri: Uri?, selection: String?): String? {
        var path: String? = null
        val cursor =
            this@MainActivity.contentResolver.query(
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

    // Display pictures according to picture path
    private fun displayImage(imagePath: String?) {
        imagePath?.let {
            GlobalScope.launch {
                uploadPhoto(
                    imagePath,
                    "http://192.168.139.113:8080/OldPhotoRestoration_war_exploded/restoration-servlet"
                )
            }
        }
    }

    private suspend fun restorePhoto(image: Bitmap, url: String) =
        withContext(Dispatchers.IO) {
            val file = convertToFile(image, this@MainActivity)
            val img: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
            val requestBody: RequestBody =
                MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.path, img)
                    .build()
            val request: Request? = Request.Builder().url(url).post(requestBody).build()
            val okHttpClient: OkHttpClient =
                OkHttpClient.Builder()
                    .readTimeout(1, TimeUnit.HOURS)
                    // .writeTimeout(1, TimeUnit.HOURS)
                    .connectTimeout(1, TimeUnit.HOURS)
                    .build()

            val call: Call = okHttpClient.newCall(request)

            call.enqueue(
                object : Callback {
                    override fun onFailure(call: Call?, e: IOException) {
                        Log.d("DebugPhotoRestorationApp", e.toString())
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call?, response: Response) {
                        val buffer = ByteArrayOutputStream()
                        var nRead: Int? = null
                        val data = ByteArray(1024)
                        while (response.body()?.byteStream()?.read(data, 0, data.size).also {
                            if (it != null) {
                                nRead = it
                            }
                        } != -1) {
                            nRead?.let { buffer.write(data, 0, it) }
                        }
                        buffer.flush()
                        val byteArray = buffer.toByteArray()
                        try {
                            val bitmapRestored =
                                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                            val photoToInsert =
                                Photo(
                                    //                  BitmapFactory.decodeFile(imagePath),
                                    image,
                                    bitmapRestored,
                                    null,
                                    null,
                                    null,
                                    null
                                )
                            mViewModel.addPhoto(photoToInsert)
                            Log.d("DebugPhotoRestorationApp", "Successfully added to database")
                        } catch (e: Exception) {
                            e.message?.let { Log.d("DebugPhotoRestorationApp", it) }
                        }
                    }
                }
            )
        }

    private suspend fun uploadPhoto(imagePath: String, url: String) =
        withContext(Dispatchers.IO) {
            val file = File(imagePath)
            val image: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
            val requestBody: RequestBody =
                MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", imagePath, image)
                    .build()
            val request: Request? = Request.Builder().url(url).post(requestBody).build()
            val okHttpClient: OkHttpClient =
                OkHttpClient.Builder()
                    .readTimeout(1, TimeUnit.HOURS)
                    // .writeTimeout(1, TimeUnit.HOURS)
                    .connectTimeout(1, TimeUnit.HOURS)
                    .build()

            val call: Call = okHttpClient.newCall(request)

            call.enqueue(
                object : Callback {
                    override fun onFailure(call: Call?, e: IOException) {
                        Log.d("DebugPhotoRestorationApp", e.toString())
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call?, response: Response) {
                        val buffer = ByteArrayOutputStream()
                        var nRead: Int? = null
                        val data = ByteArray(1024)
                        while (response.body()?.byteStream()?.read(data, 0, data.size).also {
                            if (it != null) {
                                nRead = it
                            }
                        } != -1) {
                            nRead?.let { buffer.write(data, 0, it) }
                        }
                        buffer.flush()
                        val byteArray = buffer.toByteArray()
                        try {
                            val bitmapRestored =
                                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                            val photoToInsert =
                                Photo(
                                    BitmapFactory.decodeFile(imagePath),
                                    bitmapRestored,
                                    null,
                                    null,
                                    null,
                                    null
                                )
                            mViewModel.addPhoto(photoToInsert)
                            Log.d("DebugPhotoRestorationApp", "Successfully added to database")
                        } catch (e: Exception) {
                            e.message?.let { Log.d("DebugPhotoRestorationApp", it) }
                        }
                    }
                }
            )
        }
}
