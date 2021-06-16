package com.example.oldphotorestorationapplication.galleries

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.imagepickerlibrary.ImagePickerActivityClass
import com.app.imagepickerlibrary.ImagePickerBottomsheet
import com.app.imagepickerlibrary.*
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.authentication.AuthenticationActivity
import com.example.oldphotorestorationapplication.databinding.GalleriesBinding
import com.example.oldphotorestorationapplication.gallery.PhotoGalleryFragment
import com.example.oldphotorestorationapplication.people.PeopleGalleryFragment
import com.example.oldphotorestorationapplication.photorestorationsettings.PhotoRestorationSettingsActivity

class GalleriesActivity: AppCompatActivity(), ImagePickerBottomsheet.ItemClickListener, ImagePickerActivityClass.OnResult {

    private lateinit var binding: GalleriesBinding
    private lateinit var imagePicker: ImagePickerActivityClass
    private lateinit var mViewModel: GalleriesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GalleriesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mViewModel = ViewModelProvider(this).get(GalleriesViewModel::class.java)

        val photoGalleryFragment = PhotoGalleryFragment()
        val peopleGalleryFragment = PeopleGalleryFragment()

        setCurrentFragment(photoGalleryFragment)

        binding.bottomNavigation.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.ic_people -> setCurrentFragment(peopleGalleryFragment)
                R.id.ic_photos -> setCurrentFragment(photoGalleryFragment)
                R.id.ic_restore -> {
                    val fragment = ImagePickerBottomsheet()
                    fragment.show(supportFragmentManager, bottomSheetActionFragment)
                }
            }
            true
        }

        imagePicker = ImagePickerActivityClass(this, this, this, activityResultRegistry)
        imagePicker.cropOptions(true)
    }

    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }

    //IMAGE PICKER
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
//            setButtonColors(galleryButtonColor = ContextCompat.getColor(requireContext(), R.color.white))
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
        val intent = Intent(this, PhotoRestorationSettingsActivity::class.java)
        intent.putExtra("imagePath", item?.path)
        startActivity(intent)
    }
    //IMAGE PICKER


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.signOut){
            mViewModel.signOut()
            val intent = Intent(this, AuthenticationActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}