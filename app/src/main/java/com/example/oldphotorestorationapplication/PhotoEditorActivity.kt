package com.example.oldphotorestorationapplication

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.oldphotorestorationapplication.data.Photo
import com.example.oldphotorestorationapplication.data.PhotoViewModel
import com.example.oldphotorestorationapplication.databinding.PhotoEditorBinding

class PhotoEditorActivity : AppCompatActivity() {
    private lateinit var binding: PhotoEditorBinding
    private lateinit var mViewModel: PhotoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PhotoEditorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    private fun init() {
        mViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        val id: Int = intent.getIntExtra("photoId", -1)
        if (id > 0) {
            mViewModel
                .findPhotoById(id)
                .observe(
                    this,
                    { currentPhoto ->
                        currentPhoto?.let { setView(currentPhoto) }
                    }
                )
        }
    }

    private fun setView(photo: Photo) {
            val viewPagerAdapter =
                ViewPagerAdapter(this, arrayOf(photo.restoredPhoto, photo.initialPhoto))
            binding.viewPager.adapter = viewPagerAdapter
            binding.editTextTitle.setText(photo.title)
            binding.editTextDescription.setText(photo.description)
            binding.editTextDate.setText(photo.date)
            binding.editTextLocation.setText(photo.location)

            binding.buttonSave.setOnClickListener {
                photo.title =
                    if (binding.editTextTitle.text.toString().trim().isNotEmpty()) {
                        binding.editTextTitle.text.toString()
                    } else {
                        null
                    }
                photo.description =
                    if (binding.editTextDescription.text.toString().trim().isNotEmpty()) {
                        binding.editTextDescription.text.toString()
                    } else {
                        null
                    }
                photo.date =
                    if (binding.editTextDate.text.toString().trim().isNotEmpty()) {
                        binding.editTextDate.text.toString()
                    } else {
                        null
                    }
                photo.location =
                    if (binding.editTextLocation.text.toString().trim().isNotEmpty()) {
                        binding.editTextLocation.text.toString()
                    } else {
                        null
                    }
                mViewModel.updatePhoto(photo)
                finish()
            }

            binding.buttonDelete.setOnClickListener {
                val builder: AlertDialog.Builder =
                    this@PhotoEditorActivity.let { AlertDialog.Builder(it) }
                builder.setPositiveButton("Yes") { _, _ ->
                    mViewModel.deletePhoto(photo)
                    Toast.makeText(applicationContext, "Successfully removed", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
                builder.setNegativeButton("No") { _, _ -> }
                builder.setMessage("Are you sure you want to delete this photo?")
                builder.create().show()
            }
    }
}
