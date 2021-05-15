package com.example.oldphotorestorationapplication

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oldphotorestorationapplication.data.Photo
import java.util.*


interface OnPhotoClickListener{
    fun onPhotoClick()
}


class RecyclerViewAdapter(
    private val courseDataArrayList: ArrayList<Photo>,
    private val mcontext: Context,
    private val listener: OnPhotoClickListener
) :
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        // Inflate Layout
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        // Set the data to textview and imageview.
        val recyclerData = courseDataArrayList[position]
        holder.photoTitle.text = recyclerData.title
        holder.photo.setImageBitmap(BitmapFactory.decodeByteArray(recyclerData.restoredPhoto, 0, recyclerData.restoredPhoto.size))
//        holder.photo.setImageBitmap(recyclerData.restoredPhoto)
        holder.itemView.setOnClickListener{listener.onPhotoClick()}
    }

    override fun getItemCount(): Int {
        // this method returns the size of recyclerview
        return courseDataArrayList.size
    }

    // View Holder Class to handle Recycler View.
    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoTitle: TextView
        val photo: ImageView

        init {
            photoTitle = itemView.findViewById(R.id.idTVCourse)
            photo = itemView.findViewById(R.id.idIVcourseIV)
        }
    }
}