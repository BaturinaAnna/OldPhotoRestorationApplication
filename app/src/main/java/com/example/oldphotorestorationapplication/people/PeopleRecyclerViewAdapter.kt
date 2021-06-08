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

class PeopleRecyclerViewAdapter(private val clickListener: OnPersonClickListener) :
    RecyclerView.Adapter<PeopleRecyclerViewAdapter.RecyclerViewHolder>() {

    private var peopleList = mutableListOf<Face>()
    private lateinit var mapNameFace:  Map<String?, List<Face>>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_person_layout, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = peopleList[position]
        holder.itemView.personFace.setImageBitmap(currentItem.face)
        holder.itemView.personName.text = currentItem.name
        holder.itemView.setOnClickListener {
            clickListener.onPersonClick(position, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return peopleList.size
    }

    fun setData(people: List<Face>) {
        groupPeopleByNames(people)
        notifyDataSetChanged()
    }

    private fun mapNamesWithFaces(people: List<Face>){
        mapNameFace = people.groupBy { it.name }
    }

    private fun groupPeopleByNames(people: List<Face>) {
        mapNamesWithFaces(people)
        mapNameFace.forEach { (t, u) ->
            peopleList.add(u[0])
        }
    }

    fun getFaceByPosition(position: Int): Face{
        return mapNameFace.values.toList()[position][0]
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
