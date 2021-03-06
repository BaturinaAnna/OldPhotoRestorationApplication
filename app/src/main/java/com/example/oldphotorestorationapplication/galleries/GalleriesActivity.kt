package com.example.oldphotorestorationapplication.galleries

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.imagepickerlibrary.*
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.authentication.AuthenticationActivity
import com.example.oldphotorestorationapplication.databinding.GalleriesBinding
import com.example.oldphotorestorationapplication.facedetails.FaceDetailsActivity
import com.example.oldphotorestorationapplication.peoplegallery.PeopleGalleryFragment
import com.example.oldphotorestorationapplication.photoeditor.PhotoEditorActivity
import com.example.oldphotorestorationapplication.photogallery.PhotoGalleryFragment
import com.example.oldphotorestorationapplication.photorestorationsettings.PhotoRestorationSettingsActivity
import com.example.oldphotorestorationapplication.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class GalleriesActivity :
    AppCompatActivity(),
    ImagePickerBottomsheet.ItemClickListener,
    ImagePickerActivityClass.OnResult {

    enum class DisplayedFragment { PEOPLE, PHOTOS }

    private lateinit var binding: GalleriesBinding
    private lateinit var imagePicker: ImagePickerActivityClass
    private lateinit var mViewModel: GalleriesViewModel
    private lateinit var currentFragmentType: DisplayedFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GalleriesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mViewModel = ViewModelProvider(this).get(GalleriesViewModel::class.java)

        val photoGalleryFragment = PhotoGalleryFragment()
        val peopleGalleryFragment = PeopleGalleryFragment()

        setCurrentFragment(photoGalleryFragment)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
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

    override fun onResume() {
        super.onResume()
        when (intent.getSerializableExtra("currentFragment")) {
            DisplayedFragment.PHOTOS -> setCurrentFragment(PhotoGalleryFragment())
            DisplayedFragment.PEOPLE -> setCurrentFragment(PeopleGalleryFragment())
        }
    }

    override fun onPause() {
        super.onPause()
        intent.putExtra("currentFragment", currentFragmentType)
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
        when (fragment) {
            is PeopleGalleryFragment -> currentFragmentType = DisplayedFragment.PEOPLE
            is PhotoGalleryFragment -> currentFragmentType = DisplayedFragment.PHOTOS
        }
    }

    internal fun openPersonDetails(idFace: Long) {
        val intent = Intent(this@GalleriesActivity, FaceDetailsActivity::class.java)
        intent.putExtra("faceId", idFace)
        this.startActivity(intent)
    }

    internal fun openPhotoDetails(idPhoto: Long?) {
        val intent = Intent(this@GalleriesActivity, PhotoEditorActivity::class.java)
        intent.putExtra("photoId", idPhoto)
        this.startActivity(intent)
    }

    internal fun shareBitmap(bitmap: Bitmap) {
        val cachePath = File(externalCacheDir, "my_images/")
        cachePath.mkdirs()

        // create png file
        val file = File(cachePath, "photo.png")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()

        val myImageFileUri =
            FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".provider",
                file
            )

        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri)
        intent.type = "image/png"
        startActivity(Intent.createChooser(intent, "Share to: "))
    }

    // IMAGE PICKER
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

    override fun doCustomisations(fragment: ImagePickerBottomsheet) {
        fragment.apply {
            setButtonText(
                cameraButtonText = "Select Camera",
                galleryButtonText = "Select Gallery",
                cancelButtonText = "Cancel"
            )
            setButtonColors(cameraButtonColor = ContextCompat.getColor(requireContext(), R.color.dark_brown))
            setButtonColors(galleryButtonColor = ContextCompat.getColor(requireContext(), R.color.dark_brown))
            setButtonColors(cancelButtonColor = ContextCompat.getColor(requireContext(), R.color.dark_brown))
            setBottomSheetBackgroundStyle(R.color.light_brown)
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
        val intent = Intent(this, PhotoRestorationSettingsActivity::class.java)
        intent.putExtra("imagePath", item?.path)
        startActivity(intent)
    }
    // IMAGE PICKER

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.signOut) {
            showAlertDialog(
                context = this@GalleriesActivity,
                message = resources.getString(R.string.sure_want_to_exit),
                actionsPositive = { _, _ ->
                    mViewModel.signOut()
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    startActivity(intent)
                    finish() },
                actionsNegative = {_, _ ->},
            )

        }
        return super.onOptionsItemSelected(item)
    }
}
