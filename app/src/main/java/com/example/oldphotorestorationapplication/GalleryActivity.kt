package com.example.oldphotorestorationapplication


import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.imagepickerlibrary.*
import com.example.oldphotorestorationapplication.data.Photo
import com.example.oldphotorestorationapplication.data.PhotoViewModel
import com.example.oldphotorestorationapplication.databinding.GalleryBinding
import java.io.File
import java.io.FileOutputStream


class GalleryActivity : AppCompatActivity(), OnPhotoClickListener, OnPhotoLongClickListener, ImagePickerBottomsheet.ItemClickListener, ImagePickerActivityClass.OnResult  {

    private lateinit var binding: GalleryBinding
    private lateinit var mViewModel: PhotoViewModel
    private lateinit var imagePicker: ImagePickerActivityClass
    private lateinit var adapterPhoto: PhotoRecyclerViewAdapter
    private var foundPhotosList: List<Photo>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GalleryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // in this method '2' represents number of columns to be displayed in grid view.
        val layoutManager = GridLayoutManager(this, 2)
        binding.photoRecyclerView.layoutManager = layoutManager

        adapterPhoto = PhotoRecyclerViewAdapter(this, this)
        binding.photoRecyclerView.adapter = adapterPhoto

        mViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        mViewModel.allPhotos.observe(this, { photo -> adapterPhoto.setData(photo) })
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


    override fun onPhotoClick(position: Int, view: View) {
        val photo = when(foundPhotosList){
            null -> mViewModel.allPhotos.value?.get(position)
            else -> foundPhotosList!![position]
        }
        val intent = Intent(view.context, PhotoEditorActivity::class.java)
        intent.putExtra("photoId", photo?.id)
        view.context.startActivity(intent)
    }


    private fun init() {
        binding.buttonRestore.setOnClickListener{
                val fragment = ImagePickerBottomsheet()
                fragment.show(supportFragmentManager, bottomSheetActionFragment)
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
                        val allPhotos = mViewModel.allPhotos.value
                        val foundPhotos = mutableListOf<Photo>()
                        val query: String = newText.lowercase()
                        allPhotos?.forEach {
                            if (it.title?.lowercase()?.contains(query) == true) {
                                foundPhotos.add(it)
                            } else if (it.description?.lowercase()?.contains(query) == true){
                                foundPhotos.add(it)
                            } else if (it.date?.lowercase()?.contains(query) == true){
                                foundPhotos.add(it)
                            } else if (it.location?.lowercase()?.contains(query) == true){
                                foundPhotos.add(it)
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
