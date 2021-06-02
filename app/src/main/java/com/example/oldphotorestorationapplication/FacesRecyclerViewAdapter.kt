package com.example.oldphotorestorationapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.oldphotorestorationapplication.data.Face
import kotlinx.android.synthetic.main.face_layout.view.*
import kotlinx.android.synthetic.main.item_layout.view.*


interface OnFaceClickListener {
    fun onFaceClick(position: Int, view: View)
}

class FacesRecyclerViewAdapter(
    private val clickListener: OnFaceClickListener
) : RecyclerView.Adapter<FacesRecyclerViewAdapter.RecyclerViewHolder>() {

    private var faceList = emptyList<Face>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.face_layout, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = faceList[position]
        holder.itemView.faceItemImageView.setImageBitmap(currentItem.face)
        holder.itemView.setOnClickListener { clickListener.onFaceClick(position, holder.itemView) }
    }

    override fun getItemCount(): Int {
        return faceList.size
    }

    fun setData(faces: List<Face>) {
        this.faceList = faces
        notifyDataSetChanged()
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}