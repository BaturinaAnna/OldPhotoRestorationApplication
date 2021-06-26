package com.example.oldphotorestorationapplication.photogallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.data.firebase.photo.PhotoFirebase
import kotlinx.android.synthetic.main.photo_item.view.*

val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<PhotoFirebase>() {
    override fun areItemsTheSame(oldItem: PhotoFirebase, newItem: PhotoFirebase): Boolean =
        oldItem.idPhoto == newItem.idPhoto

    override fun areContentsTheSame(oldItem: PhotoFirebase, newItem: PhotoFirebase): Boolean =
        oldItem == newItem
}

interface OnPhotoClickListener {
    fun onPhotoClick(itemClicked: PhotoFirebase)
}

interface OnPhotoLongClickListener {
    fun onLongPhotoClick(position: Int, view: View): Boolean
}

class PhotoPagingDataAdapter(
    private val clickListener: OnPhotoClickListener,
    private val longClickListener: OnPhotoLongClickListener
) : PagingDataAdapter<PhotoFirebase, PhotoFirebaseViewHolder>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoFirebaseViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotoFirebaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoFirebaseViewHolder, position: Int) {
        val repoItem = getItem(position)
        if (repoItem != null) {
            holder.itemView.photoItemImageView.setImageBitmap(repoItem.restoredPhoto)
            holder.itemView.setOnClickListener { clickListener.onPhotoClick(repoItem) }
            holder.itemView.setOnLongClickListener{longClickListener.onLongPhotoClick(position, holder.itemView)}
        }
    }


}

class PhotoFirebaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)