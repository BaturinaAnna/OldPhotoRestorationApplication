package com.example.oldphotorestorationapplication.facedetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.data.photo.Photo
import com.example.oldphotorestorationapplication.people.PeopleRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_person_layout.view.*
import kotlinx.android.synthetic.main.item_person_layout.view.personName
import kotlinx.android.synthetic.main.item_photo_for_face.view.*
import kotlinx.android.synthetic.main.person_editor.view.*

interface OnPhotoForFaceClickListener {
    fun onPhotoForFaceClick(position: Int, view: View)
}

interface OnPhotoForFaceLongClickListener {
    fun onPhotoForFaceLongClick(position: Int, view: View): Boolean
}
class PhotosForFaceRecyclerViewAdapter(
    private val clickListener: OnPhotoForFaceClickListener,
    private val longClickListener: OnPhotoForFaceLongClickListener
): RecyclerView.Adapter<PhotosForFaceRecyclerViewAdapter.RecyclerViewHolder>() {

    private var photosList = emptyList<Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_photo_for_face, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = photosList[position]
        holder.itemView.photoForFace.setImageBitmap(currentItem.restoredPhoto)
        holder.itemView.setOnClickListener{ clickListener.onPhotoForFaceClick(position, holder.itemView) }
        holder.itemView.setOnLongClickListener { longClickListener.onPhotoForFaceLongClick(position, holder.itemView) }
    }

    override fun getItemCount(): Int {
        return photosList.size
    }

    fun setData(photos: List<Photo>) {
        photosList = photos
        notifyDataSetChanged()
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}