package com.example.oldphotorestorationapplication.facedetails

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.databinding.PersonEditorBinding
import com.example.oldphotorestorationapplication.people.PeopleRecyclerViewAdapter
import com.example.oldphotorestorationapplication.people.PeopleViewModel
import com.example.oldphotorestorationapplication.photoeditor.PhotoEditorViewModel


class FaceDetailsActivity: AppCompatActivity() {
    private lateinit var binding: PersonEditorBinding
    private lateinit var mViewModel: FaceDetailsViewModel
    private lateinit var currentFace: Face
    private lateinit var adapterPhotos: PhotosForFaceRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PersonEditorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewPhotosForFace.layoutManager = layoutManager

        adapterPhotos = PhotosForFaceRecyclerViewAdapter()
        binding.recyclerViewPhotosForFace.adapter = adapterPhotos

        mViewModel = ViewModelProvider(this).get(FaceDetailsViewModel::class.java)

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
                    val listPhotosWithFace = photos.filter {
                        it.faces.any { it.name == currentFace.name}
                    }.map{it.photo}.toList()
                    adapterPhotos.setData(listPhotosWithFace)
            })
        }
    }

    private fun setView(face: Face){
        binding.personFace.setImageBitmap(face.face)
        binding.personName.text = face.name
    }
}