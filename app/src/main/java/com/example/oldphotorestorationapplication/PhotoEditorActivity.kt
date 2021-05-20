package com.example.oldphotorestorationapplication

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.oldphotorestorationapplication.data.Photo
import com.example.oldphotorestorationapplication.data.PhotoViewModel

class PhotoEditorActivity : AppCompatActivity() {
  private lateinit var editTextTitle: TextView
  private lateinit var editTextDescription: TextView
  private lateinit var editTextDate: TextView
  private lateinit var editTextLocation: TextView
  private lateinit var buttonSave: Button
  private lateinit var buttonCancel: Button
  private lateinit var viewPager: ViewPager
  private lateinit var mViewModel: PhotoViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.photo_editor)
    init()
  }

  private fun init() {
    editTextTitle = findViewById(R.id.editTextTitle)
    editTextDescription = findViewById(R.id.editTextDescription)
    editTextDate = findViewById(R.id.editTextDate)
    editTextLocation = findViewById(R.id.editTextLocation)
    buttonSave = findViewById(R.id.buttonSave)
    buttonCancel = findViewById(R.id.buttonCancel)
    viewPager = findViewById(R.id.viewPager)
    mViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
    val id: Int = intent.getIntExtra("photoId", -1)
    if (id > 0) {
      mViewModel
          .findPhotoById(id)
          .observe(
              this,
              { currentPhoto -> // your code here
                setView(currentPhoto)
              })
    }
  }

  private fun setView(photo: Photo?) {
    if (photo != null) {
      val viewPagerAdapter =
          ViewPagerAdapter(this, arrayOf(photo.restoredPhoto, photo.initialPhoto))
      viewPager.adapter = viewPagerAdapter
      editTextTitle.text = photo.title
      editTextDescription.text = photo.description
      editTextDate.text = photo.date
      editTextLocation.text = photo.location

      buttonSave.setOnClickListener {
        if (editTextTitle.text.toString().isNotEmpty()) {
          photo.title = editTextTitle.text.toString()
        } else {
          photo.title = null
        }
        if (editTextDescription.text.toString().isNotEmpty()) {
          photo.description = editTextDescription.text.toString()
        } else {
          photo.description = null
        }
        if (editTextDate.text.toString().isNotEmpty()) {
          photo.date = editTextDate.text.toString()
        } else {
          photo.date = null
        }
        if (editTextLocation.text.toString().isNotEmpty()) {
          photo.location = editTextLocation.text.toString()
        } else {
          photo.location = null
        }
        mViewModel.updatePhoto(photo)
        finish()
      }

      buttonCancel.setOnClickListener {
        val builder: AlertDialog.Builder = this@PhotoEditorActivity.let { AlertDialog.Builder(it) }

        //              val builder = AlertDialog.Builder(applicationContext)
        builder.setPositiveButton("Yes") { _, _ ->
          mViewModel.deletePhoto(photo)
          Toast.makeText(applicationContext, "Successfully removed", Toast.LENGTH_SHORT).show()
          finish()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setMessage("Are you sure you want to delete this photo?")
        builder.create().show()
      }
    }
  }
}
