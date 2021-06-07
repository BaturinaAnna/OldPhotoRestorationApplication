package com.example.oldphotorestorationapplication.people

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.imagepickerlibrary.*
import com.example.oldphotorestorationapplication.PhotoRecyclerViewAdapter
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.databinding.GalleryBinding
import com.example.oldphotorestorationapplication.databinding.PeopleGalleryActivityBinding
import com.example.oldphotorestorationapplication.gallery.GalleryActivity
import com.example.oldphotorestorationapplication.photoeditor.PhotoEditorActivity
import com.example.oldphotorestorationapplication.photorestorationsettings.PhotoRestorationSettingsActivity

class PeopleGalleryActivity: AppCompatActivity(), OnPersonClickListener, ImagePickerBottomsheet.ItemClickListener, ImagePickerActivityClass.OnResult{
    private lateinit var binding: PeopleGalleryActivityBinding
    private lateinit var imagePicker: ImagePickerActivityClass
    private lateinit var adapterPeople: PeopleRecyclerViewAdapter
    private lateinit var mViewModel: PeopleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PeopleGalleryActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // in this method '2' represents number of columns to be displayed in grid view.
        val layoutManager = LinearLayoutManager(this)
        binding.peopleRecyclerView.layoutManager = layoutManager

        adapterPeople = PeopleRecyclerViewAdapter(this)
        binding.peopleRecyclerView.adapter = adapterPeople

        mViewModel = ViewModelProvider(this).get(PeopleViewModel::class.java)
        mViewModel.allFaces.observe(this, { faces -> adapterPeople.setData(faces)})

        init()

        imagePicker = ImagePickerActivityClass(this, this, this, activityResultRegistry)
        imagePicker.cropOptions(true)

    }

    override fun onPersonClick(position: Int, view: View) {
        Toast.makeText(applicationContext, "HI I AM PERSON", Toast.LENGTH_SHORT).show()
    }

    private fun init(){
        binding.bottomNavigation.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.ic_people -> {
                    binding.peopleRecyclerView.scrollToPosition(0)
                    true
                }
                R.id.ic_photos -> {
                    val intent = Intent(this, GalleryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.ic_restore -> {
                    val fragment = ImagePickerBottomsheet()
                    fragment.show(supportFragmentManager, bottomSheetActionFragment)
                    true
                }
                else -> {false}
            }
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
}