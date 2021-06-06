package com.example.oldphotorestorationapplication.photorestorationsettings

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.imagepickerlibrary.*
import com.example.oldphotorestorationapplication.databinding.RestorationSettingsBinding


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
            mViewModel.restoreAndSavePhoto(imagePath, binding.switchRemoveScratches.isChecked.toString())
            finish()
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
