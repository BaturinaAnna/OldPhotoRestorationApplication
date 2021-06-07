package com.example.oldphotorestorationapplication.people

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.data.face.Face
import kotlinx.android.synthetic.main.item_layout.view.*
import kotlinx.android.synthetic.main.item_person_layout.view.*


interface OnPersonClickListener {
    fun onPersonClick(position: Int, view: View)
}

class PeopleRecyclerViewAdapter(
    private val clickListener: OnPersonClickListener
) : RecyclerView.Adapter<PeopleRecyclerViewAdapter.RecyclerViewHolder>() {

    private var peopleList = emptyList<Face>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_person_layout, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = peopleList[position]
        holder.itemView.personFace.setImageBitmap(currentItem.face)
        holder.itemView.setOnClickListener { clickListener.onPersonClick(position, holder.itemView) }
    }

    override fun getItemCount(): Int {
        return peopleList.size
    }

    fun setData(people: List<Face>) {
        this.peopleList = people
        notifyDataSetChanged()
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
