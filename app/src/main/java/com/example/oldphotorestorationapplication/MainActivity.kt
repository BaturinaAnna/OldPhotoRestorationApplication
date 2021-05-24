package com.example.oldphotorestorationapplication


import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.imagepickerlibrary.*
import com.example.oldphotorestorationapplication.data.PhotoViewModel
import com.example.oldphotorestorationapplication.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.item_layout.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity(), OnPhotoClickListener, OnPhotoLongClickListener, ImagePickerBottomsheet.ItemClickListener, ImagePickerActivityClass.OnResult  {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mViewModel: PhotoViewModel
    private lateinit var imagePicker: ImagePickerActivityClass


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // in this method '2' represents number of columns to be displayed in grid view.
        val layoutManager = GridLayoutManager(this, 2)
        binding.photoRecyclerView.layoutManager = layoutManager

        val adapter = RecyclerViewAdapter(this, this)
//        val adapter = RecyclerViewAdapter(this, this)
        binding.photoRecyclerView.adapter = adapter

        mViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        mViewModel.allData.observe(this, { photo -> adapter.setData(photo) })
        init()

        imagePicker = ImagePickerActivityClass(this, this, this, activityResultRegistry)
        imagePicker.cropOptions(true)

//        registerForContextMenu(binding.photoRecyclerView)
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
            setButtonColors(galleryButtonColor = ContextCompat.getColor(requireContext(), R.color.white))
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
        val photo = mViewModel.allData.value?.get(position)
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
                    val photo = mViewModel.allData.value?.get(position)
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setPositiveButton("Yes") { _, _ ->
                        mViewModel.deletePhoto(photo!!)
                        Toast.makeText(applicationContext, "Successfully removed", Toast.LENGTH_SHORT)
                            .show()
                    }
                    builder.setNegativeButton("No") { _, _ -> }
                    builder.setMessage("Are you sure you want to delete this photo?")
                    builder.create().show()
                    true
                }
                R.id.sharePhotoMenu -> {
                    val photo = mViewModel.allData.value?.get(position)
                    shareBitmap(photo!!.restoredPhoto)
//                    val intent = Intent()
//                    intent.action = Intent.ACTION_SEND
////                    intent.putExtra(Intent.)
//                    intent.putExtra(Intent.EXTRA_TEXT, "try send intent in android")
//                    intent.type = "image/*"
//                    startActivity(Intent.createChooser(intent, "Share to: "))
//                    Toast.makeText(applicationContext, "PRESS SHARE IN MENU", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> true
            }
        }
        popupMenu.show()
        return true
    }

    private fun shareBitmap(bitmap: Bitmap) {
        //---Save bitmap to external cache directory---//
        //get cache directory
        val cachePath = File(externalCacheDir, "my_images/")
        cachePath.mkdirs()

        //create png file
        val file = File(cachePath, "Image_123.png")
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //---Share File---//
        //get file uri
        val myImageFileUri =
            FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", file)

        //create a intent
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri)
        intent.type = "image/png"
        startActivity(Intent.createChooser(intent, "Share to: "))
    }


//    override fun onMenuDeleteClickListener(item: MenuItem): Boolean {
////        Toast.makeText(applicationContext, "DELETE PRESSED IN MENU", Toast.LENGTH_SHORT).show()
//        Toast.makeText(applicationContext, ph, Toast.LENGTH_SHORT).show()
//        return true
//    }
//
//    override fun onMenuShareClickListener(item: MenuItem): Boolean {
//        Toast.makeText(applicationContext, "SHARE PRESSED IN MENU", Toast.LENGTH_SHORT).show()
//        return true
//    }

}
