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
import android.renderscript.RenderScript.Priority
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.*
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var recyclerDataArrayList: ArrayList<PhotoInfo>? = null
    private var buttonToRestore: Button? = null

    private var popupView: View? = null
    private var popupWindow: PopupWindow? = null
    private var editTextTitle: TextView? = null
    private var photoImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonToRestore = findViewById(R.id.buttonRestore)
        recyclerView = findViewById(R.id.idCourseRV)

        // created new array list..
        recyclerDataArrayList = ArrayList()

        // added data from arraylist to adapter class.
        val adapter = RecyclerViewAdapter(recyclerDataArrayList!!, this)

        // setting grid layout manager to implement grid view.
        // in this method '2' represents number of columns to be displayed in grid view.
        val layoutManager = GridLayoutManager(this, 2)

        // at last set adapter to recycler view.
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = adapter

        recyclerView!!.addOnItemTouchListener(RecyclerItemClickListener(this, recyclerView!!,
            object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Log.d("ANNA", "CLICK")
                showPopupWindow(recyclerView)
            }
            override fun onItemLongClick(view: View?, position: Int) {
                Log.d("ANNA", "LONG CLICK")
            }
        }))
        init()
    }

    private fun init() {
        buttonToRestore!!.setOnClickListener {
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

    private fun setView(view: View) {
        val inflater = view.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.photo_editor, null)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        val focusable = true
        popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow!!.showAtLocation(view, Gravity.CENTER, 0, 0)
        editTextTitle = popupView!!.findViewById(R.id.editTextTitle)
        photoImageView = popupView!!.findViewById(R.id.photo)
    }

    fun showPopupWindow(view: View?) {
        setView(view!!)
        photoImageView!!.setImageBitmap(recyclerDataArrayList?.get(0)?.restoredPhoto)
        editTextTitle!!.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                editTextTitle!!.text = ""
            }
        }
        editTextTitle!!.onFocusChangeListener =
            OnFocusChangeListener { v: View?, hasFocus: Boolean ->
                if (hasFocus) {
                    editTextTitle!!.text = ""
                }
            }
//        buttonAdd.setOnClickListener { v ->
//            var priority: Priority? = Priority.HIGH
//            if (radioButtonMedium.isChecked()) {
//                priority = Priority.MEDIUM
//            } else if (radioButtonLow.isChecked()) {
//                priority = Priority.LOW
//            }
//            val notice = Notice(
//                editTextTitle!!.text.toString(),
//                editTextNote.getText().toString(),
//                priority
//            )
//            NoticeInsertTask().execute(notice)
//            notices.add(notice)
//            popupWindow!!.dismiss()
//        }
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
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openAlbum()
        } else {
            Toast.makeText(applicationContext, "You denied the permission", Toast.LENGTH_SHORT)
                .show()
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
            CoroutineScope(IO).launch {
                uploadPhoto(
                    imagePath,
                    "http://192.168.1.235:8080/OldPhotoRestoration_war_exploded/restoration-servlet"
                )
            }
        } else {
            Toast.makeText(
                applicationContext,
                "failed to get or download image",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private suspend fun uploadPhoto(imagePath: String, url: String) {
        withContext(IO) {
            val file: File = File(imagePath)
            val image: RequestBody? = RequestBody.create(MediaType.parse("image/*"), file)
            val requestBody: RequestBody? = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imagePath, image)
                .build()
            val request: Request? = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.HOURS)
                //.writeTimeout(1, TimeUnit.HOURS)
                .connectTimeout(1, TimeUnit.HOURS)
                .build()

            val call: Call = okHttpClient.newCall(request)

            call.enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException) {
                    Log.d("ANNA", e.toString())
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
                    try {
                        val bitmapRestored = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                        recyclerDataArrayList!!.add(PhotoInfo("Photo", bitmapRestored))
                        GlobalScope.launch(Dispatchers.Main){
                            recyclerView!!.adapter!!.notifyDataSetChanged()
                        }
                    } catch (e: Exception) {
                        e.message?.let { Log.d("ANNA", it) }
                    }
                }
            })
        }
    }
}