package com.example.oldphotorestorationapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewpager.widget.ViewPager
import com.example.oldphotorestorationapplication.data.Photo
import com.example.oldphotorestorationapplication.data.PhotoViewModel
import java.lang.Exception

class PhotoEditorActivity : AppCompatActivity() {

  private lateinit var editTextTitle: TextView
  private lateinit var editTextDescription: TextView
  private lateinit var editTextDate: TextView
  private lateinit var editTextLocation: TextView
  private lateinit var buttonSave: Button
  private lateinit var buttonCancel: Button
  private lateinit var viewPager: ViewPager
  private lateinit var mViewModel: PhotoViewModel

  private lateinit var photo: Photo
  private lateinit var photos: List<Photo>


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.photo_editor)


    editTextTitle = findViewById(R.id.editTextTitle)
    editTextDescription = findViewById(R.id.editTextDescription)
    editTextDate = findViewById(R.id.editTextDate)
    editTextLocation = findViewById(R.id.editTextLocation)
    buttonSave = findViewById(R.id.buttonSave)
    buttonCancel = findViewById(R.id.buttonCancel)
    viewPager = findViewById(R.id.viewPager)

//    mViewModel = ViewModelProvider(this.parent as ViewModelStoreOwner).get(PhotoViewModel::class.java)
//    mViewModel.readAllData.observe(this, {photosL -> run { photos = photosL } })
//    Toast.makeText(applicationContext, photos.size.toString(), Toast.LENGTH_SHORT).show()

    Toast.makeText(applicationContext, mViewModel.readAllData.value.toString(), Toast.LENGTH_SHORT).show()

//    mViewModel.findPhotoById(2).value.
//    Toast.makeText(applicationContext, intent.getIntExtra("photoId", 0).toString(), Toast.LENGTH_SHORT).show()
//    Toast.makeText(applicationContext, mViewModel.findPhotoById(intent.getIntExtra("photoId", 0)).value.toString(), Toast.LENGTH_SHORT).show()
//    Toast.makeText(applicationContext, photo.toString(), Toast.LENGTH_SHORT).show()

//    Toast.makeText(applicationContext, mViewModel.findPhotoById(photo.id).value?.title.toString(), Toast.LENGTH_SHORT).show()

//    Toast.makeText(applicationContext, photo.title.toString(), Toast.LENGTH_SHORT).show()
//    val viewPagerAdapter =
//        ViewPagerAdapter(this, arrayOf(photo?.restoredPhoto, photo?.initialPhoto))
//    viewPager.adapter = viewPagerAdapter
//
//    editTextTitle.text = photo?.title
//    editTextDescription.text = photo?.description
//    editTextDate.text = photo?.date
//    editTextLocation.text = photo?.location
//
//    buttonSave.setOnClickListener {
//      if (editTextTitle.text.toString().isNotEmpty()) {
//        photo?.title = editTextTitle.text.toString()
//      } else {
//        photo?.title = null
//      }
//      if (editTextDescription.text.toString().isNotEmpty()) {
//        photo?.description = editTextDescription.text.toString()
//      } else {
//        photo?.description = null
//      }
//      if (editTextDate.text.toString().isNotEmpty()) {
//        photo?.date = editTextDate.text.toString()
//      } else {
//        photo?.date = null
//      }
//      if (editTextLocation.text.toString().isNotEmpty()) {
//        photo?.location = editTextLocation.text.toString()
//      } else {
//        photo?.location = null
//      }
//
//      mViewModel.updatePhoto(photo!!)
//    }
//    buttonCancel.setOnClickListener {
//      //      mViewModel.deletePhoto(photo!!)
//    }

  }
}
