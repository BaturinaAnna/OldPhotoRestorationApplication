package com.example.oldphotorestorationapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.imagepickerlibrary.*
import com.example.oldphotorestorationapplication.data.Photo
import com.example.oldphotorestorationapplication.data.PhotoViewModel
import com.example.oldphotorestorationapplication.databinding.RestorationSettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class PhotoRestorationSettingsActivity: AppCompatActivity(), ImagePickerBottomsheet.ItemClickListener, ImagePickerActivityClass.OnResult {
    private lateinit var binding: RestorationSettingsBinding
    private lateinit var imagePath: String
    private lateinit var mViewModel: PhotoViewModel
    private lateinit var imagePicker: ImagePickerActivityClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RestorationSettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        init()

        imagePicker = ImagePickerActivityClass(this, this, this, activityResultRegistry)
        imagePicker.cropOptions(true)
    }

    private fun init(){
        imagePath = intent.getStringExtra("imagePath").toString()
        binding.photoToRestore.setImageBitmap(BitmapFactory.decodeFile(imagePath))
        binding.photoToRestore.setOnLongClickListener{
            val fragment = ImagePickerBottomsheet()
            fragment.show(supportFragmentManager, bottomSheetActionFragment)
            true
        }
        binding.buttonRestore.setOnClickListener{
            GlobalScope.launch {
                uploadPhoto(
                    imagePath,
                    binding.switchRemoveScratches.isChecked.toString(),
                    "http://192.168.20.161:8080/OldPhotoRestoration_war_exploded/restoration-servlet")
            }
        }
    }

//    IMAGE PICKER
    override fun onItemClick(item: String?) {
        when {
            item.toString() == bottomSheetActionCamera -> {
                imagePicker.takePhotoFromCamera()
            }
            item.toString() == bottomSheetActionGallary -> {
                imagePicker.choosePhotoFromGallery()
            }
        }
    }

    //Override this method for customization of bottomsheet
    override fun doCustomisations(fragment: ImagePickerBottomsheet) {
        fragment.apply {
            //Customize button text
            setButtonText(cameraButtonText = "Select Camera", galleryButtonText = "Select Gallery", cancelButtonText = "Cancel")
            //Customize button text color
            setButtonColors(galleryButtonColor = ContextCompat.getColor(requireContext(), R.color.white))
            //For more customization make a style in your styles xml and pass it to this method. (This will override above method result).
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                setTextAppearance(R.style.fontForNotificationLandingPage)
//            }
            //To customize bottomsheet style
            setBottomSheetBackgroundStyle(R.drawable.drawable_bottom_sheet_dialog)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imagePicker.onActivityResult(requestCode, resultCode, data)
    }

    override fun returnString(item: Uri?) {
        binding.photoToRestore.setImageBitmap(BitmapFactory.decodeFile(item?.path))
    }
//    IMAGE PICKER


    private suspend fun uploadPhoto(imagePath: String, removeScratches:String, url: String) =
        withContext(Dispatchers.IO) {
            val file = File(imagePath)
            val image: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
            val requestBody: RequestBody =
                MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("photoToRestore", imagePath, image)
                    .addFormDataPart("removeScratches", removeScratches)
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
                        Log.d("ANNA", e.toString())
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
                            finish()
                        } catch (e: Exception) {
                            e.message?.let { Log.d("ANNA", it) }
                        }
                    }
                }
            )
        }
}