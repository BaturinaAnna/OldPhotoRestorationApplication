package com.example.oldphotorestorationapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.oldphotorestorationapplication.data.Photo
import kotlinx.android.synthetic.main.item_layout.view.*

interface OnPhotoClickListener {
    fun onPhotoClick(position: Int, view: View)
}

class RecyclerViewAdapter(
    //    private val courseDataArrayList: ArrayList<Photo>,
    //    private val mcontext: Context,
    private val listener: OnPhotoClickListener
) : RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    private var photoList = emptyList<Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = photoList[position]
        holder.itemView.photoItemImageView.setImageBitmap(currentItem.restoredPhoto)
        holder.itemView.setOnClickListener { listener.onPhotoClick(position, holder.itemView) }
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    fun setData(photo: List<Photo>) {
        this.photoList = photo
        notifyDataSetChanged()
    }

    // View Holder Class to handle Recycler View.
    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        val photoTitle: TextView
        //        val photo: ImageView
        //
        //        init {
        //            photoTitle = itemView.findViewById(R.id.photoItemTitle)
        //            photo = itemView.findViewById(R.id.photoItemImageView)
        //        }
    }
}
