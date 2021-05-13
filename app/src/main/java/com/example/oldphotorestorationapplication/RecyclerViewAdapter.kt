package com.example.oldphotorestorationapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class RecyclerViewAdapter(
    private val courseDataArrayList: ArrayList<RecyclerData>,
    private val mcontext: Context
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
        holder.courseTV.text = recyclerData.title
//        holder.courseIV.setImageResource(recyclerData.imgid)
        holder.courseIV.setImageBitmap(recyclerData.image)
    }

    override fun getItemCount(): Int {
        // this method returns the size of recyclerview
        return courseDataArrayList.size
    }

    // View Holder Class to handle Recycler View.
    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseTV: TextView
        val courseIV: ImageView

        init {
            courseTV = itemView.findViewById(R.id.idTVCourse)
            courseIV = itemView.findViewById(R.id.idIVcourseIV)
        }
    }
}