package com.example.oldphotorestorationapplication.photoeditor

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.databinding.FacePopupWindowBinding
import com.example.oldphotorestorationapplication.databinding.PhotoEditorBinding
import com.example.oldphotorestorationapplication.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.face_popup_window.*

@AndroidEntryPoint
class PhotoEditorActivity : AppCompatActivity(), OnFaceClickListener {
    private lateinit var binding: PhotoEditorBinding
    private lateinit var mViewModel: PhotoEditorViewModel
    private lateinit var editingPhoto: Photo
    private lateinit var bindingFacePopupWindow: FacePopupWindowBinding
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
        mViewModel = ViewModelProvider(this).get(PhotoEditorViewModel::class.java)
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
                { faces -> namesList = faces.map { it.name }.filterNotNull().toSet().toList() }
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
                message = resources.getString(R.string.save_changes),
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
                message = resources.getString(R.string.sure_want_to_exit),
                actionsPositive = { _, _ ->
                    mViewModel.deletePhoto(photo)
                    Toast.makeText(applicationContext, "Successfully removed", Toast.LENGTH_SHORT).show()
                    finish() },
                actionsNegative = { _, _ -> }
            )
        }
    }

    private fun checkUnsavedChanges(photo: Photo): Boolean {
        return checkUnsavedChangesTextField(binding.editTextTitle, photo.title) ||
                checkUnsavedChangesTextField(binding.editTextDescription, photo.description)||
                checkUnsavedChangesTextField(binding.editTextDate, photo.date) ||
                checkUnsavedChangesTextField(binding.editTextLocation, photo.location)
    }

    private fun updatePhotoInfo(photo: Photo): Photo {
        photo.title = getValueToUpdateTextField(binding.editTextTitle)
        photo.description = getValueToUpdateTextField(binding.editTextDescription)
        photo.date = getValueToUpdateTextField(binding.editTextDate)
        photo.location = getValueToUpdateTextField(binding.editTextLocation)
        return photo
    }

    override fun onFaceClick(position: Int, view: View) {
        var face = facesList[position]
        bindingFacePopupWindow = FacePopupWindowBinding.inflate(layoutInflater)
        popupWindow =
            PopupWindow(
                bindingFacePopupWindow.root,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true)
        popupWindow.setOnDismissListener {
            if (checkUnsavedChangesFace(face)) {
                showAlertDialog(
                    context = this@PhotoEditorActivity,
                    message = resources.getString(R.string.save_changes),
                    actionsPositive = { _, _ ->
                        face = updateFaceInfo(face)
                        mViewModel.updateFace(face)
                        Toast.makeText(applicationContext, "Changes saved", Toast.LENGTH_SHORT).show() },
                    actionsNegative = { _, _ -> }
                )
            }
        }
        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        bindingFacePopupWindow.imageView.setImageBitmap(face.face)

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, namesList)

        bindingFacePopupWindow.autoCompleteTextFaceName.setAdapter(arrayAdapter)

        face.name.let { bindingFacePopupWindow.autoCompleteTextFaceName.setText(face.name) }

        bindingFacePopupWindow.buttonCancel.setOnClickListener {
            if (checkUnsavedChangesFace(face)) {
                showAlertDialog(
                    context = this@PhotoEditorActivity,
                    message = resources.getString(R.string.save_changes),
                    actionsPositive = {_, _ ->
                        face = updateFaceInfo(face)
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
            if (checkUnsavedChangesFace(face)) {
                face = updateFaceInfo(face)
            }
            mViewModel.updateFace(face)
            popupWindow.dismiss()
        }
    }

    private fun checkUnsavedChangesFace(face: Face): Boolean {
        return checkUnsavedChangesTextField(bindingFacePopupWindow.autoCompleteTextFaceName, face.name)
    }

    private fun updateFaceInfo(face: Face): Face {
        face.name = getValueToUpdateTextField(bindingFacePopupWindow.autoCompleteTextFaceName)
        return face
    }

    private fun getValueToUpdateTextField(textView: TextView): String?{
        return with(textView.text.toString().trim()) {
            when {
                isNotEmpty() -> textView.text.toString()
                else -> null
            }
        }
    }

    private fun checkUnsavedChangesTextField(textView: TextView, currentValueInDB: String?): Boolean{
        return with(textView.text.toString().trim()) {
            when {
                isNotEmpty() && (this != currentValueInDB) -> true
                isEmpty() && (currentValueInDB != null) -> true
                else -> false
            }
        }
    }
}
