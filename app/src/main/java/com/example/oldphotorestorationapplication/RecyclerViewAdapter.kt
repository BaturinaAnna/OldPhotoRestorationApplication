package com.example.oldphotorestorationapplication

import android.view.*
import android.widget.ShareActionProvider
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.oldphotorestorationapplication.data.Photo
import kotlinx.android.synthetic.main.item_layout.view.*


//import sun.util.locale.provider.LocaleProviderAdapter.getAdapter


interface OnPhotoClickListener {
    fun onPhotoClick(position: Int, view: View)
}

interface OnPhotoLongClickListener {
    fun onLongPhotoClick(position: Int, view: View): Boolean
}

//interface OnMenuDeleteClickListener{
//    fun onMenuDeleteClickListener(item: MenuItem): Boolean
//}
//
//interface OnMenuShareClickListener{
//    fun onMenuShareClickListener(item: MenuItem): Boolean
//}

class RecyclerViewAdapter(
    private val clickListener: OnPhotoClickListener,
    private val longClickListener: OnPhotoLongClickListener
//    private val menuDeleteClickListener: OnMenuDeleteClickListener,
//    private val menuShareClickListener: OnMenuShareClickListener
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
        holder.itemView.setOnClickListener { clickListener.onPhotoClick(position, holder.itemView) }
        holder.itemView.setOnLongClickListener{longClickListener.onLongPhotoClick(position, holder.itemView)}

//        holder.itemView.setOnCreateContextMenuListener { menu, v, menuInfo ->
//            menu.add("delete").setOnMenuItemClickListener { item: MenuItem ->
//                Toast.makeText(holder.itemView.context, "PRESS DELETE IN MENU", Toast.LENGTH_SHORT).show()
//                true
////                menuDeleteClickListener.onMenuDeleteClickListener(menu.getItem(0))
//            }
//            menu.add("share").setOnMenuItemClickListener { item: MenuItem ->
//                Toast.makeText(holder.itemView.context, "PRESS SHARE IN MENU", Toast.LENGTH_SHORT).show()
//                true
////                menuShareClickListener.onMenuShareClickListener(menu.getItem(1))
//            }
//        }
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
