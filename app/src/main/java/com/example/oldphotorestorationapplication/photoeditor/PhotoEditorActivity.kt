package com.example.oldphotorestorationapplication.photoeditor

import android.R
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.oldphotorestorationapplication.ViewPagerAdapter
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.firebase.photo.PhotoFirebase
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.databinding.FacePopupWindowBinding
import com.example.oldphotorestorationapplication.databinding.PhotoEditorBinding
import com.example.oldphotorestorationapplication.showAlertDialog
import kotlinx.android.synthetic.main.face_popup_window.*

class PhotoEditorActivity : AppCompatActivity(), OnFaceClickListener {
    private lateinit var binding: PhotoEditorBinding
    private lateinit var mViewModel: PhotoEditorViewModel
    private lateinit var editingPhoto: PhotoFirebase
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
        val id: String? = intent.getStringExtra("photoId")
        id?.let {
            mViewModel
                .getPhotoById(id)
                .observe(this, { currentPhoto -> currentPhoto?.let {
                        setView(currentPhoto)
                        Log.d("ANNA", "IN OBSERVER........." + currentPhoto.toString())
                    }
                })
//
//            mViewModel
//                .findFacesByPhotoId(id)
//                .observe(
//                    this,
//                    { faces ->
//                        adapterFaces.setData(faces)
//                        facesList = faces
//                    }
//                )
//
//            mViewModel.allFaces.observe(
//                this,
//                { faces -> namesList = faces.map { it.name }.filterNotNull().toSet().toList() }
//            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        Log.d("ANNA", "BACK PRESSED")
        if (checkUnsavedChanges(editingPhoto)) {
            showAlertDialog(
                context = this@PhotoEditorActivity,
                message = "There are unsaved changes. Do you want to save them?",
                actionsPositive = { _, _ ->
                        editingPhoto = updatePhotoInfo(editingPhoto)
                        mViewModel.updatePhotoInfo(editingPhoto)
                        Toast.makeText(applicationContext, "Changes saved", Toast.LENGTH_SHORT).show()
                        finish() },
                actionsNegative = { _, _ -> finish() },
            )
        } else {
            finish()
        }
    }

    private fun setView(photo: PhotoFirebase) {
        editingPhoto = photo
        val viewPagerAdapter =
            ViewPagerAdapter(this, arrayOf(photo.restoredPhoto, photo.initialPhoto))
        binding.viewPager.adapter = viewPagerAdapter
        Log.d("ANNA", photo.idPhoto)
        binding.editTextTitle.setText(photo.title)
        binding.editTextDescription.setText(photo.description)
        binding.editTextDate.setText(photo.date)
        binding.editTextLocation.setText(photo.location)

        binding.facesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.buttonSave.setOnClickListener {
            editingPhoto = updatePhotoInfo(photo)
            mViewModel.updatePhotoInfo(photo)
            finish()
        }

//        binding.buttonDelete.setOnClickListener {
//            showAlertDialog(
//                context = this@PhotoEditorActivity,
//                message = "Are you sure you want to delete this photo?",
//                actionsPositive = { _, _ ->
//                    mViewModel.deletePhoto(photo)
//                    Toast.makeText(applicationContext, "Successfully removed", Toast.LENGTH_SHORT).show()
//                    finish() },
//                actionsNegative = { _, _ -> }
//            )
//        }
    }

    private fun checkUnsavedChanges(photo: PhotoFirebase): Boolean {
        return checkUnsavedChangesTextField(binding.editTextTitle, photo.title) ||
                checkUnsavedChangesTextField(binding.editTextDescription, photo.description)||
                checkUnsavedChangesTextField(binding.editTextDate, photo.date) ||
                checkUnsavedChangesTextField(binding.editTextLocation, photo.location);
    }

    private fun updatePhotoInfo(photo: PhotoFirebase): PhotoFirebase {
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
//        popupWindow.setOnDismissListener {
//            if (checkUnsavedChangesFace(face)) {
//                showAlertDialog(
//                    context = this@PhotoEditorActivity,
//                    message = "There are unsaved changes. Do you want to save them?",
//                    actionsPositive = { _, _ ->
//                        face = updateFaceInfo(face)
//                        mViewModel.updateFace(face)
//                        Toast.makeText(applicationContext, "Changes saved", Toast.LENGTH_SHORT).show() },
//                    actionsNegative = { _, _ -> }
//                )
//            }
//        }
        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        bindingFacePopupWindow.imageView.setImageBitmap(face.face)

        val arrayAdapter = ArrayAdapter(this, R.layout.simple_list_item_1, namesList)

        bindingFacePopupWindow.autoCompleteTextFaceName.setAdapter(arrayAdapter)

        face.name.let { bindingFacePopupWindow.autoCompleteTextFaceName.setText(face.name) }

//        bindingFacePopupWindow.buttonCancel.setOnClickListener {
//            if (checkUnsavedChangesFace(face)) {
//                showAlertDialog(
//                    context = this@PhotoEditorActivity,
//                    message = "There are unsaved changes. Do you want to save them?",
//                    actionsPositive = {_, _ ->
//                        face = updateFaceInfo(face)
//                        mViewModel.updateFace(face)
//                        Toast.makeText(applicationContext, "Changes saved", Toast.LENGTH_SHORT).show()
//                        popupWindow.dismiss()},
//                    actionsNegative = { _, _ -> popupWindow.dismiss()},
//                )
//            } else{
//                popupWindow.dismiss()
//            }
//        }
//
//        bindingFacePopupWindow.buttonSave.setOnClickListener {
//            if (checkUnsavedChangesFace(face)) {
//                face = updateFaceInfo(face)
//            }
//            mViewModel.updateFace(face)
//            popupWindow.dismiss()
//        }
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
