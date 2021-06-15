package com.example.oldphotorestorationapplication.people

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.databinding.PeopleGalleryFragmentBinding
import com.example.oldphotorestorationapplication.facedetails.FaceDetailsActivity

class PeopleGalleryFragment: Fragment(R.layout.people_gallery_fragment), OnPersonClickListener{

    private lateinit var binding: PeopleGalleryFragmentBinding
    private lateinit var adapterPeople: PeopleRecyclerViewAdapter
    private lateinit var mViewModel: PeopleViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PeopleGalleryFragmentBinding.inflate(layoutInflater)
        val view = binding.root
        init()
        return view
    }

    private fun init(){
        val layoutManager = LinearLayoutManager(this.context)
        binding.peopleRecyclerView.layoutManager = layoutManager

        adapterPeople = PeopleRecyclerViewAdapter(this)
        binding.peopleRecyclerView.adapter = adapterPeople

        mViewModel = ViewModelProvider(this).get(PeopleViewModel::class.java)
        mViewModel.allFacesWithNames.observe(viewLifecycleOwner, { faces -> adapterPeople.setData(faces)})
    }

    override fun onPersonClick(position: Int, view: View) {
        val face = adapterPeople.getFaceByPosition(position)
        val intent = Intent(view.context, FaceDetailsActivity::class.java)
        intent.putExtra("faceId", face.idFace)
        view.context.startActivity(intent)
    }
}