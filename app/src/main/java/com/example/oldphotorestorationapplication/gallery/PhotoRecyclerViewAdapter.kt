package com.example.oldphotorestorationapplication

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.oldphotorestorationapplication.data.photo.Photo
import kotlinx.android.synthetic.main.item_layout.view.*


interface OnPhotoClickListener {
    fun onPhotoClick(position: Int, view: View)
}

interface OnPhotoLongClickListener {
    fun onLongPhotoClick(position: Int, view: View): Boolean
}

class PhotoRecyclerViewAdapter(
    private val clickListener: OnPhotoClickListener,
    private val longClickListener: OnPhotoLongClickListener
) : RecyclerView.Adapter<PhotoRecyclerViewAdapter.RecyclerViewHolder>() {

    private var photoList = emptyList<Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = photoList[position]
        holder.itemView.photoItemImageView.setImageBitmap(currentItem.restoredPhoto)
        holder.itemView.setOnClickListener { clickListener.onPhotoClick(position, holder.itemView) }
        holder.itemView.setOnLongClickListener{longClickListener.onLongPhotoClick(position, holder.itemView)}
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    fun setData(photo: List<Photo>) {
        this.photoList = photo
        notifyDataSetChanged()
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
