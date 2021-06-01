package com.example.oldphotorestorationapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.oldphotorestorationapplication.data.Face
import kotlinx.android.synthetic.main.face_layout.view.*
import kotlinx.android.synthetic.main.item_layout.view.*

class FacesRecyclerViewAdapter() : RecyclerView.Adapter<FacesRecyclerViewAdapter.RecyclerViewHolder>() {

    private var faceList = emptyList<Face>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.face_layout, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = faceList[position]
        holder.itemView.faceItemImageView.setImageBitmap(currentItem.face)
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