package com.example.oldphotorestorationapplication.gallery


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.imagepickerlibrary.*
import com.example.oldphotorestorationapplication.*
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.data.photowithfaces.PhotoWithFaces
import com.example.oldphotorestorationapplication.databinding.GalleryBinding
import com.example.oldphotorestorationapplication.people.PeopleGalleryActivity
import com.example.oldphotorestorationapplication.photoeditor.PhotoEditorActivity
import com.example.oldphotorestorationapplication.photorestorationsettings.PhotoRestorationSettingsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class GalleryActivity : AppCompatActivity(), OnPhotoClickListener, OnPhotoLongClickListener, ImagePickerBottomsheet.ItemClickListener, ImagePickerActivityClass.OnResult  {

    private lateinit var binding: GalleryBinding
    private lateinit var mViewModel: GalleryViewModel
    private lateinit var imagePicker: ImagePickerActivityClass
    private lateinit var adapterPhoto: PhotoRecyclerViewAdapter
    private lateinit var allPhotoWithFaces: List<PhotoWithFaces>
    private var foundPhotosList: List<Photo>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GalleryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        binding.photoRecyclerView.layoutManager = layoutManager

        adapterPhoto = PhotoRecyclerViewAdapter(this, this)
        binding.photoRecyclerView.adapter = adapterPhoto

        mViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        mViewModel.allPhotos.observe(this, { photos ->
//            val compressedPhotos = photos.map{
//                Photo(initialPhoto = it.initialPhoto,
//                    restoredPhoto = resizeImage(it.restoredPhoto),
//                    title = it.title,
//                    description = it.description,
//                    date = it.date,
//                    location = it.location,
//                    id = it.idPhoto)
//            }
//            adapterPhoto.setData(compressedPhotos)
            adapterPhoto.setData(photos)
        })
        mViewModel.allPhotoWithFaces.observe(this, { photoWithFaces ->
            allPhotoWithFaces = photoWithFaces
        })
        init()

        imagePicker = ImagePickerActivityClass(this, this, this, activityResultRegistry)
        imagePicker.cropOptions(true)

    }

    //IMAGE PICKER

    override fun onItemClick(item: String?) {
        when {
            item.toString() == bottomSheetActionCamera -> {
                imagePicker.takePhotoFromCamera()
            }
            item.toString() == bottomSheetActionGallary -> {
                imagePicker.choosePhotoFromGallery()
            }
        }
    }

    //Override this method for customization of bottomsheet
    override fun doCustomisations(fragment: ImagePickerBottomsheet) {
        fragment.apply {
            //Customize button text
            setButtonText(cameraButtonText = "Select Camera", galleryButtonText = "Select Gallery", cancelButtonText = "Cancel")
            //Customize button text color
//            setButtonColors(galleryButtonColor = ContextCompat.getColor(requireContext(), R.color.white))
            //For more customization make a style in your styles xml and pass it to this method. (This will override above method result).
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                setTextAppearance(R.style.fontForNotificationLandingPage)
//            }
            //To customize bottomsheet style
            setBottomSheetBackgroundStyle(R.drawable.drawable_bottom_sheet_dialog)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imagePicker.onActivityResult(requestCode, resultCode, data)
    }

    override fun returnString(item: Uri?) {
        val intent = Intent(this, PhotoRestorationSettingsActivity::class.java)
        intent.putExtra("imagePath", item?.path)
        startActivity(intent)
    }

    //IMAGE PICKER


    override fun onPhotoClick(position: Int) {
        val photo = when(foundPhotosList){
            null -> mViewModel.allPhotos.value?.get(position)
            else -> foundPhotosList!![position]
        }
        val intent = Intent(this, PhotoEditorActivity::class.java)
        intent.putExtra("photoId", photo?.idPhoto)
        this.startActivity(intent)
    }


    private fun init() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.ic_people -> {
                    val intent = Intent(this, PeopleGalleryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.ic_photos -> {
                    binding.photoRecyclerView.scrollToPosition(0)
                    true
                }
                R.id.ic_restore -> {
                    val fragment = ImagePickerBottomsheet()
                    fragment.show(supportFragmentManager, bottomSheetActionFragment)
                    true
                }
                else -> {false}
            }
        }
    }

    override fun onLongPhotoClick(position: Int, view: View): Boolean {
        val popupMenu = PopupMenu(applicationContext, view)
        popupMenu.inflate(R.menu.menu)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.deletePhotoMenu -> {
                    val photo = mViewModel.allPhotos.value?.get(position)
                    showAlertDialog(
                        context = this@GalleryActivity,
                        message = "Are you sure you want to delete this photo?",
                        actionsPositive = { _, _ ->
                            mViewModel.deletePhoto(photo!!)
                            Toast.makeText(applicationContext, "Successfully removed", Toast.LENGTH_SHORT).show() },
                        actionsNegative = { _, _ -> },
                    )
                    true
                }
                R.id.sharePhotoMenu -> {
                    val photo = mViewModel.allPhotos.value?.get(position)
                    shareBitmap(photo!!.restoredPhoto)
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

    @ExperimentalStdlibApi
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val menuItem = menu?.findItem(R.id.search)
        if(menuItem != null){
            val searchView = menuItem.actionView as SearchView

            menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    foundPhotosList = null
                    searchView.setQuery("", true)
                    searchView.clearFocus()
                    return true
                }
            })

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.trim().isNotEmpty()){
                        val foundPhotos = mutableListOf<Photo>()
                        val query: String = newText.lowercase()
                        allPhotoWithFaces.forEach {
                            if (it.photo.title?.lowercase()?.contains(query) == true) {
                                foundPhotos.add(it.photo)
                            } else if (it.photo.description?.lowercase()?.contains(query) == true){
                                foundPhotos.add(it.photo)
                            } else if (it.photo.date?.lowercase()?.contains(query) == true){
                                foundPhotos.add(it.photo)
                            } else if (it.photo.location?.lowercase()?.contains(query) == true){
                                foundPhotos.add(it.photo)
                            } else {
                                val names = it.faces.map{it.name}.toList()
                                val photo = it.photo
                                names.let {
                                    for(name in names){
                                        if (name?.lowercase()?.contains(query) == true){
                                            foundPhotos.add(photo)
                                            break
                                        }
                                    }
                                }
                            }
                        }
                        adapterPhoto.setData(foundPhotos)
                        foundPhotosList = foundPhotos
                    } else {
                        foundPhotosList = mViewModel.allPhotos.value
                        mViewModel.allPhotos.value?.let { adapterPhoto.setData(it) }
                    }
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }
}
