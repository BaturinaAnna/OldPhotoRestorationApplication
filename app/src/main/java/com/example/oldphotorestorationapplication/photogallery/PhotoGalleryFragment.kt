package com.example.oldphotorestorationapplication.photogallery

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.oldphotorestorationapplication.*
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.data.photowithfaces.PhotoWithFaces
import com.example.oldphotorestorationapplication.databinding.PhotoGalleryFragmentBinding
import com.example.oldphotorestorationapplication.galleries.GalleriesActivity


class PhotoGalleryFragment :
    Fragment(R.layout.photo_gallery_fragment), OnPhotoClickListener, OnPhotoLongClickListener {

    private lateinit var binding: PhotoGalleryFragmentBinding
    lateinit var adapterPhoto: PhotoPagingDataAdapter
//    private lateinit var mViewModel: PhotoGalleryViewModel
    private lateinit val mViewModel: RemoteViewModel
    private lateinit var allPhotoWithFaces: List<PhotoWithFaces>
    private var foundPhotosList: List<Photo>? = null

    @ExperimentalPagingApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PhotoGalleryFragmentBinding.inflate(layoutInflater)
        val view = binding.root
        init()
        return view
    }

    @ExperimentalPagingApi
    private fun init() {
        val layoutManager = GridLayoutManager(this.context, 2)
        binding.photoRecyclerView.layoutManager = layoutManager

        //        adapterPhoto = PhotoRecyclerViewAdapter(this, this)
        val mViewModel = ViewModelProvider(this).get(RemoteViewModel::class.java)
        adapterPhoto = PhotoPagingDataAdapter(this, this)

        mViewModel.photosPaginated.observe(
            viewLifecycleOwner, { photos ->
                lifecycleScope.launchWhenCreated {
                    adapterPhoto.submitData(photos)
                }
            }
        )

        binding.photoRecyclerView.adapter = adapterPhoto

//        mViewModel = ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)
        //        mViewModel.allPhotos.observe(viewLifecycleOwner, { photos ->
        ////            val compressedPhotos = photos.map{
        ////                Photo(initialPhoto = it.initialPhoto,
        ////                    restoredPhoto = resizeImage(it.restoredPhoto),
        ////                    title = it.title,
        ////                    description = it.description,
        ////                    date = it.date,
        ////                    location = it.location,
        ////                    id = it.idPhoto)
        ////            }
        ////            adapterPhoto.setData(compressedPhotos)
        //            adapterPhoto.setData(photos)
        //        })


        mViewModel.allPhotoWithFaces.observe(
            viewLifecycleOwner,
            { photoWithFaces -> allPhotoWithFaces = photoWithFaces }
        )

        setHasOptionsMenu(true)
    }

    @ExperimentalPagingApi
    override fun onPhotoClick(position: Int) {
        val idClickedPhoto =
            when (foundPhotosList) {
                null -> adapterPhoto.getItemId(position)
                else -> foundPhotosList!![position]
            }
        (activity as GalleriesActivity).openPhotoDetails(photo?.idPhoto)
    }

    override fun onLongPhotoClick(position: Int, view: View): Boolean {
        val popupMenu = PopupMenu(this.context, view)
        popupMenu.inflate(R.menu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.deletePhotoMenu -> {
                    val photo = mViewModel.allPhotos.value?.get(position)
                    this.context?.let { context ->
                        showAlertDialog(
                            context = context,
                            message = "Are you sure you want to delete this photo?",
                            actionsPositive = { _, _ ->
                                mViewModel.deletePhoto(photo!!)
                                Toast.makeText(context, "Successfully removed", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            actionsNegative = { _, _ -> },
                        )
                    }
                    true
                }
                R.id.sharePhotoMenu -> {
                    val photo = mViewModel.allPhotos.value?.get(position)
                    (activity as GalleriesActivity).shareBitmap(photo!!.restoredPhoto)
                    true
                }
                else -> true
            }
        }
        popupMenu.show()
        return true
    }

    @ExperimentalStdlibApi
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        val menuItem = menu.findItem(R.id.search)
        if (menuItem != null) {
            val searchView = menuItem.actionView as SearchView

            menuItem.setOnActionExpandListener(
                object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                        foundPhotosList = null
                        searchView.setQuery("", true)
                        searchView.clearFocus()
                        return true
                    }
                }
            )

            searchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchView.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText!!.trim().isNotEmpty()) {
                            val foundPhotos = mutableListOf<Photo>()
                            val query: String = newText.lowercase()
                            allPhotoWithFaces.forEach {
                                if (it.photo.title?.lowercase()?.contains(query) == true) {
                                    foundPhotos.add(it.photo)
                                } else if (it.photo.description?.lowercase()?.contains(query) ==
                                        true
                                ) {
                                    foundPhotos.add(it.photo)
                                } else if (it.photo.date?.lowercase()?.contains(query) == true) {
                                    foundPhotos.add(it.photo)
                                } else if (it.photo.location?.lowercase()?.contains(query) == true
                                ) {
                                    foundPhotos.add(it.photo)
                                } else {
                                    val names = it.faces.map { it.name }.toList()
                                    val photo = it.photo
                                    names.let {
                                        for (name in names) {
                                            if (name?.lowercase()?.contains(query) == true) {
                                                foundPhotos.add(photo)
                                                break
                                            }
                                        }
                                    }
                                }
                            }
                            //                        adapterPhoto.setData(foundPhotos)
                            foundPhotosList = foundPhotos
                        } else {
                            foundPhotosList = mViewModel.allPhotos.value
                            //                        mViewModel.allPhotos.value?.let {
                            // adapterPhoto.setData(it) }
                        }
                        return true
                    }
                }
            )
        }
        super.onCreateOptionsMenu(menu, inflater)
    }
}
