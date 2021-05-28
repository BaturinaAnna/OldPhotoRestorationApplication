package com.example.oldphotorestorationapplication

import android.R
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.lifecycle.ViewModelProvider
import com.example.oldphotorestorationapplication.data.Photo
import com.example.oldphotorestorationapplication.data.PhotoViewModel
import com.example.oldphotorestorationapplication.databinding.PhotoEditorBinding


class PhotoEditorActivity : AppCompatActivity() {
    private lateinit var binding: PhotoEditorBinding
    private lateinit var mViewModel: PhotoViewModel
    private lateinit var editingPhoto: Photo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PhotoEditorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true);
        }
    }

    private fun init() {
        mViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        val id: Int = intent.getIntExtra("photoId", -1)
        if (id > 0) {
            mViewModel
                .findPhotoById(id)
                .observe(this, { currentPhoto -> currentPhoto?.let { setView(currentPhoto) } })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (checkUnsavedChanges(editingPhoto)) {
            val builder: AlertDialog.Builder =
                this@PhotoEditorActivity.let { AlertDialog.Builder(it) }
            builder.setPositiveButton("Yes") { _, _ ->
                editingPhoto = updatePhotoInfo(editingPhoto)
                mViewModel.updatePhoto(editingPhoto)
                Toast.makeText(applicationContext, "Changes saved", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
            builder.setNegativeButton("No") { _, _ ->
                finish()
            }
            builder.setMessage("There are unsaved changes. Do you want to save them?")
            builder.create().show()
        } else {
            finish()
        }
    }

    private fun setView(photo: Photo) {
        editingPhoto = photo
        val viewPagerAdapter =
            ViewPagerAdapter(this, arrayOf(photo.restoredPhoto, photo.initialPhoto))
        binding.viewPager.adapter = viewPagerAdapter
        binding.editTextTitle.setText(photo.title)
        binding.editTextDescription.setText(photo.description)
        binding.editTextDate.setText(photo.date)
        binding.editTextLocation.setText(photo.location)

        binding.buttonSave.setOnClickListener {
            editingPhoto = updatePhotoInfo(photo)
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

    private fun checkUnsavedChanges(photo: Photo): Boolean {
        return with(binding.editTextTitle.text.toString().trim()){
                    when {
                        isNotEmpty() && (this != photo.title) -> true
                        isEmpty() && (photo.title != null) -> true
                        else -> false
                    }
                } ||
                with(binding.editTextDescription.text.toString().trim()){
                    when {
                        isNotEmpty() && (this != photo.description) -> true
                        isEmpty() && (photo.description != null) -> true
                        else -> false
                    }
                } ||
                with(binding.editTextDate.text.toString().trim()){
                    when {
                        isNotEmpty() && (this != photo.date) -> true
                        isEmpty() && (photo.date != null) -> true
                        else -> false
                    }
                } ||
                with(binding.editTextLocation.text.toString().trim()){
                    when {
                        isNotEmpty() && (this != photo.location) -> true
                        isEmpty() && (photo.location != null) -> true
                        else -> false
                    }
                }
    }

    private fun updatePhotoInfo(photo: Photo): Photo{
        photo.title = with (binding.editTextTitle.text.toString().trim()){
            when {
                isNotEmpty() -> binding.editTextTitle.text.toString()
                else -> null
            }
        }
        photo.description = with (binding.editTextDescription.text.toString().trim()){
            when {
                isNotEmpty() -> binding.editTextDescription.text.toString()
                else -> null
            }
        }
        photo.date = with (binding.editTextDate.text.toString().trim()){
            when {
                isNotEmpty() -> binding.editTextDate.text.toString()
                else -> null
            }
        }
        photo.location = with (binding.editTextLocation.text.toString().trim()){
            when {
                isNotEmpty() -> binding.editTextLocation.text.toString()
                else -> null
            }
        }
        return photo
    }
}
