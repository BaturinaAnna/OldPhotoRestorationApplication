package com.example.oldphotorestorationapplication.photorestorationsettings

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.imagepickerlibrary.*
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.databinding.RestorationSettingsBinding
import com.example.oldphotorestorationapplication.databinding.WaitingPopupWindowBinding
import com.example.oldphotorestorationapplication.network.NetworkRepository
import com.example.oldphotorestorationapplication.network.RestorationNetwork
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PhotoRestorationSettingsActivity :
    AppCompatActivity(),
    ImagePickerBottomsheet.ItemClickListener,
    ImagePickerActivityClass.OnResult {
    private lateinit var binding: RestorationSettingsBinding
    private lateinit var imagePath: String
    private lateinit var mViewModel: PhotoRestorationSettingsViewModel
    private lateinit var imagePicker: ImagePickerActivityClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RestorationSettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mViewModel = ViewModelProvider(this).get(PhotoRestorationSettingsViewModel::class.java)
        init()

        imagePicker = ImagePickerActivityClass(this, this, this, activityResultRegistry)
        imagePicker.cropOptions(true)
    }

    private fun init() {
        imagePath = intent.getStringExtra("imagePath").toString()
        binding.photoToRestore.setImageBitmap(BitmapFactory.decodeFile(imagePath))
        binding.photoToRestore.setOnLongClickListener {
            val fragment = ImagePickerBottomsheet()
            fragment.show(supportFragmentManager, bottomSheetActionFragment)
            true
        }
        binding.buttonRestore.setOnClickListener {
            val bindingPopupWindow = WaitingPopupWindowBinding.inflate(layoutInflater)
            val popupWindow = PopupWindow(
                bindingPopupWindow.root,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            popupWindow.showAtLocation(
                binding.root,
                Gravity.CENTER,
                0,
                0
            )

            GlobalScope.launch {
                val networkRepository = NetworkRepository(RestorationNetwork())
                val listOfPhotos = networkRepository.restorePhoto(imagePath,
                    binding.switchRemoveScratches.isChecked.toString(),
                    "http://192.168.45.230:8080/OldPhotoRestoration_war_exploded/restoration-servlet")
                val bitmapRestoredPhoto = BitmapFactory.decodeByteArray(listOfPhotos[0],
                            0,
                            listOfPhotos[0].size)
                val photoToInsert =
                    Photo(
                        BitmapFactory.decodeFile(imagePath),
                        bitmapRestoredPhoto,
                        null,
                        null,
                        null,
                        null
                    )
                if (listOfPhotos.size == 1){
                    mViewModel.addPhoto(photoToInsert)
                } else {
                    val faces: ArrayList<Face> = ArrayList()
                    for (faceByteArray in listOfPhotos.subList(1, listOfPhotos.size))
                    {
                        faces.add(
                            Face(
                            face = BitmapFactory.decodeByteArray(faceByteArray, 0, faceByteArray.size),
                            idPhoto = null,
                            name = null
                        )
                        )
                    }
                    mViewModel.addPhotoWithFaces(photoToInsert, faces)
                }
                finish()
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

    // Override this method for customization of bottomsheet
    override fun doCustomisations(fragment: ImagePickerBottomsheet) {
        fragment.apply {
            // Customize button text
            setButtonText(
                cameraButtonText = "Select Camera",
                galleryButtonText = "Select Gallery",
                cancelButtonText = "Cancel"
            )
            // Customize button text color
//            setButtonColors(
//                galleryButtonColor = ContextCompat.getColor(requireContext(), R.color.white)
//            )
            // For more customization make a style in your styles xml and pass it to this method.
            // (This will override above method result).
            //            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            // {
            //                setTextAppearance(R.style.fontForNotificationLandingPage)
            //            }
            // To customize bottomsheet style
//            setBottomSheetBackgroundStyle(R.drawable.drawable_bottom_sheet_dialog)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
}
