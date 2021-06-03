package com.example.oldphotorestorationapplication

import android.R
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.oldphotorestorationapplication.data.Face
import com.example.oldphotorestorationapplication.data.Photo
import com.example.oldphotorestorationapplication.data.PhotoViewModel
import com.example.oldphotorestorationapplication.databinding.FacePopupWindowBinding
import com.example.oldphotorestorationapplication.databinding.PhotoEditorBinding
import kotlinx.android.synthetic.main.face_popup_window.*

class PhotoEditorActivity : AppCompatActivity(), OnFaceClickListener {
    private lateinit var binding: PhotoEditorBinding
    private lateinit var mViewModel: PhotoViewModel
    private lateinit var editingPhoto: Photo
    private lateinit var adapterFaces: FacesRecyclerViewAdapter
    private lateinit var popupWindow: PopupWindow
    private lateinit var facesList: List<Face>
    private lateinit var namesList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PhotoEditorBinding.inflate(layoutInflater)
        val view = binding.root

        adapterFaces = FacesRecyclerViewAdapter(this)
        binding.facesRecyclerView.adapter = adapterFaces
        setContentView(view)

        init()

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun init() {
        mViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        val id: Long = intent.getLongExtra("photoId", -1)
        if (id > 0) {
            mViewModel
                .findPhotoById(id)
                .observe(this, { currentPhoto -> currentPhoto?.let { setView(currentPhoto) } })

            mViewModel
                .findFacesByPhotoId(id)
                .observe(
                    this,
                    { faces ->
                        adapterFaces.setData(faces)
                        facesList = faces
                    }
                )

            mViewModel.allFaces.observe(
                this,
                { faces -> namesList = faces.map { it.name }.toList().filterNotNull() }
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (checkUnsavedChanges(editingPhoto)) {
            showAlertDialog(
                context = this@PhotoEditorActivity,
                message = "There are unsaved changes. Do you want to save them?",
                actionsPositive = { _, _ ->
                        editingPhoto = updatePhotoInfo(editingPhoto)
                        mViewModel.updatePhoto(editingPhoto)
                        Toast.makeText(applicationContext, "Changes saved", Toast.LENGTH_SHORT).show()
                        finish() },
                actionsNegative = { _, _ -> finish() },
            )
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

        binding.facesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.buttonSave.setOnClickListener {
            editingPhoto = updatePhotoInfo(photo)
            mViewModel.updatePhoto(photo)
            finish()
        }

        binding.buttonDelete.setOnClickListener {
            showAlertDialog(
                context = this@PhotoEditorActivity,
                message = "Are you sure you want to delete this photo?",
                actionsPositive = { _, _ ->
                    mViewModel.deletePhoto(photo)
                    Toast.makeText(applicationContext, "Successfully removed", Toast.LENGTH_SHORT).show()
                    finish() },
                actionsNegative = { _, _ -> }
            )
        }
    }

    private fun checkUnsavedChanges(photo: Photo): Boolean {
        return with(binding.editTextTitle.text.toString().trim()) {
            when {
                isNotEmpty() && (this != photo.title) -> true
                isEmpty() && (photo.title != null) -> true
                else -> false
            }
        } ||
            with(binding.editTextDescription.text.toString().trim()) {
                when {
                    isNotEmpty() && (this != photo.description) -> true
                    isEmpty() && (photo.description != null) -> true
                    else -> false
                }
            } ||
            with(binding.editTextDate.text.toString().trim()) {
                when {
                    isNotEmpty() && (this != photo.date) -> true
                    isEmpty() && (photo.date != null) -> true
                    else -> false
                }
            } ||
            with(binding.editTextLocation.text.toString().trim()) {
                when {
                    isNotEmpty() && (this != photo.location) -> true
                    isEmpty() && (photo.location != null) -> true
                    else -> false
                }
            }
    }

    private fun updatePhotoInfo(photo: Photo): Photo {
        photo.title =
            with(binding.editTextTitle.text.toString().trim()) {
                when {
                    isNotEmpty() -> binding.editTextTitle.text.toString()
                    else -> null
                }
            }
        photo.description =
            with(binding.editTextDescription.text.toString().trim()) {
                when {
                    isNotEmpty() -> binding.editTextDescription.text.toString()
                    else -> null
                }
            }
        photo.date =
            with(binding.editTextDate.text.toString().trim()) {
                when {
                    isNotEmpty() -> binding.editTextDate.text.toString()
                    else -> null
                }
            }
        photo.location =
            with(binding.editTextLocation.text.toString().trim()) {
                when {
                    isNotEmpty() -> binding.editTextLocation.text.toString()
                    else -> null
                }
            }
        return photo
    }

    override fun onFaceClick(position: Int, view: View) {
        val face = facesList[position]

        val bindingFacePopupWindow = FacePopupWindowBinding.inflate(layoutInflater)
        popupWindow =
            PopupWindow(
                bindingFacePopupWindow.root,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true)
        popupWindow.setOnDismissListener {
            if (bindingFacePopupWindow.autoCompleteTextFaceName.text.toString() != face.name) {
                showAlertDialog(
                    context = this@PhotoEditorActivity,
                    message = "There are unsaved changes. Do you want to save them?",
                    actionsPositive = { _, _ ->
                        face.name = bindingFacePopupWindow.autoCompleteTextFaceName.text.toString()
                        mViewModel.updateFace(face)
                        Toast.makeText(applicationContext, "Changes saved", Toast.LENGTH_SHORT).show() },
                    actionsNegative = { _, _ -> }
                )
            }
        }
        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        bindingFacePopupWindow.imageView.setImageBitmap(face.face)

        val arrayAdapter = ArrayAdapter(this, R.layout.simple_list_item_1, namesList)

        bindingFacePopupWindow.autoCompleteTextFaceName.setAdapter(arrayAdapter)

        face.name.let { bindingFacePopupWindow.autoCompleteTextFaceName.setText(face.name) }

        bindingFacePopupWindow.buttonCancel.setOnClickListener {
            if (bindingFacePopupWindow.autoCompleteTextFaceName.text.toString() != face.name) {
                showAlertDialog(
                    context = this@PhotoEditorActivity,
                    message = "There are unsaved changes. Do you want to save them?",
                    actionsPositive = {_, _ ->
                        face.name = bindingFacePopupWindow.autoCompleteTextFaceName.text.toString()
                        mViewModel.updateFace(face)
                        Toast.makeText(applicationContext, "Changes saved", Toast.LENGTH_SHORT).show()
                        popupWindow.dismiss()},
                    actionsNegative = { _, _ -> popupWindow.dismiss()},
                )
            } else{
                popupWindow.dismiss()
            }
        }

        bindingFacePopupWindow.buttonSave.setOnClickListener {
            if (bindingFacePopupWindow.autoCompleteTextFaceName.text.toString() != face.name) {
                face.name = bindingFacePopupWindow.autoCompleteTextFaceName.text.toString()
            }
            mViewModel.updateFace(face)
            popupWindow.dismiss()
        }
    }
}
