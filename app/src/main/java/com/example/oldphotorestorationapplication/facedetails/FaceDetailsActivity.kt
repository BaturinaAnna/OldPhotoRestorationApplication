package com.example.oldphotorestorationapplication.facedetails

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.databinding.PersonEditorBinding
import com.example.oldphotorestorationapplication.photoeditor.PhotoEditorActivity
import com.example.oldphotorestorationapplication.showAlertDialog
import java.io.File
import java.io.FileOutputStream


class FaceDetailsActivity: AppCompatActivity(), OnPhotoForFaceClickListener, OnPhotoForFaceLongClickListener {
    private lateinit var binding: PersonEditorBinding
    private lateinit var mViewModel: FaceDetailsViewModel
    private lateinit var currentFace: Face
    private lateinit var adapterPhotos: PhotosForFaceRecyclerViewAdapter
    private lateinit var listPhotosWithFace: List<Photo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PersonEditorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewPhotosForFace.layoutManager = layoutManager

        adapterPhotos = PhotosForFaceRecyclerViewAdapter(this, this)
        binding.recyclerViewPhotosForFace.adapter = adapterPhotos

        mViewModel = ViewModelProvider(this).get(FaceDetailsViewModel::class.java)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        init()
    }

    private fun init() {
        val id: Long = intent.getLongExtra("faceId", -1)
        if (id > 0) {
            mViewModel
                .findFaceById(id)
                .observe(this, { face -> face?.let {
                        setView(face)
                        currentFace = face
                    }
                })

                mViewModel.allPhotoWithFaces.observe(this, { photos ->
                    listPhotosWithFace = photos.filter {
                        it.faces.any { it.name == currentFace.name}
                    }.map{
                        it.photo
//                        Photo(initialPhoto = it.photo.initialPhoto,
//                            restoredPhoto = resizeImage(it.photo.restoredPhoto),
//                            title = it.photo.title,
//                            description = it.photo.description,
//                            date = it.photo.date,
//                            location = it.photo.location,
//                            id = it.photo.idPhoto)
                    }
                    adapterPhotos.setData(listPhotosWithFace)
            })
        }
    }

    private fun setView(face: Face){
        binding.personFace.setImageBitmap(face.face)
        binding.personName.text = face.name
    }

    override fun onPhotoForFaceClick(position: Int, view: View) {
        val photo = listPhotosWithFace[position]
        val intent = Intent(view.context, PhotoEditorActivity::class.java)
        intent.putExtra("photoId", photo.idPhoto)
        view.context.startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onPhotoForFaceLongClick(position: Int, view: View): Boolean {
        val popupMenu = PopupMenu(applicationContext, view)
        popupMenu.inflate(R.menu.menu)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.deletePhotoMenu -> {
                    val photo = listPhotosWithFace[position]
                    showAlertDialog(
                        context = this,
                        message = "Are you sure you want to delete this photo?",
                        actionsPositive = { _, _ ->
                            mViewModel.deletePhoto(photo)
                            Toast.makeText(applicationContext, "Successfully removed", Toast.LENGTH_SHORT).show() },
                        actionsNegative = { _, _ -> },
                    )
                    true
                }
                R.id.sharePhotoMenu -> {
                    val photo = listPhotosWithFace[position]
                    shareBitmap(photo.restoredPhoto)
                    true
                }
                else -> true
            }
        }
        popupMenu.show()
        return true
    }

    private fun shareBitmap(bitmap: Bitmap) {
        val cachePath = File(externalCacheDir, "my_images/")
        cachePath.mkdirs()

        //create png file
        val file = File(cachePath, "photo.png")
        val fileOutputStream: FileOutputStream
        fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()

        val myImageFileUri =
            FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", file)

        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri)
        intent.type = "image/png"
        startActivity(Intent.createChooser(intent, "Share to: "))
    }
}