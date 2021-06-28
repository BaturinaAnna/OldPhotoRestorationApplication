package com.example.oldphotorestorationapplication.peoplegallery

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.data.face.Face
import com.example.oldphotorestorationapplication.databinding.PeopleGalleryFragmentBinding
import com.example.oldphotorestorationapplication.galleries.GalleriesActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PeopleGalleryFragment: Fragment(R.layout.people_gallery_fragment), OnPersonClickListener{

    private lateinit var binding: PeopleGalleryFragmentBinding
    private lateinit var adapterPeople: PeopleRecyclerViewAdapter
    private lateinit var mViewModel: PeopleViewModel
    private var foundFacesList: List<Face>? = null

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
        setHasOptionsMenu(true)
    }


    override fun onPersonClick(position: Int, view: View) {
        val face = when(foundFacesList){
            null -> adapterPeople.getFaceByPosition(position)
            else -> foundFacesList!![position]
        }
        (activity as GalleriesActivity).openPersonDetails(face.idFace)
    }

    @ExperimentalStdlibApi
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        val menuItem = menu.findItem(R.id.search)
        if(menuItem != null){
            val searchView = menuItem.actionView as SearchView

            menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    foundFacesList = null
                    searchView.setQuery("", true)
                    searchView.clearFocus()
                    return true
                }
            })

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.trim().isNotEmpty()){
                        val foundFaces = mutableListOf<Face>()
                        val query: String = newText.lowercase()
                        adapterPeople.getMapNameFace().entries.forEach {
                            if (it.key?.lowercase()?.contains(query) == true) {
                                foundFaces.addAll(it.value)
                            }
                        }
                        adapterPeople.setData(foundFaces)
                        foundFacesList = foundFaces
                    } else {
                        foundFacesList = mViewModel.allFacesWithNames.value
                        mViewModel.allFacesWithNames.value?.let { adapterPeople.setData(it) }
                    }
                    return true
                }
            })
        }
        super.onCreateOptionsMenu(menu,inflater)
    }
}