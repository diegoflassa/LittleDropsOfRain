package io.github.diegoflassa.littledropsofrain.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.diegoflassa.littledropsofrain.adapters.MyRecyclerViewAdapter
import io.github.diegoflassa.littledropsofrain.databinding.FragmentGalleryBinding


class GalleryFragment : Fragment(), MyRecyclerViewAdapter.ItemClickListener {

    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var adapter: MyRecyclerViewAdapter
    private lateinit var binding :FragmentGalleryBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        galleryViewModel =
            ViewModelProvider.NewInstanceFactory().create(GalleryViewModel::class.java)
        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.textGallery.text = it
        })
        // set up the RecyclerView

        // data to populate the RecyclerView with
        val animalNames: ArrayList<String> = ArrayList()
        animalNames.add("Horse")
        animalNames.add("Cow")
        animalNames.add("Camel")
        animalNames.add("Sheep")
        animalNames.add("Goat")

        // set up the RecyclerView
        binding.myRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MyRecyclerViewAdapter(context, animalNames)
        adapter.setClickListener(this)
        binding.myRecyclerView.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(
            binding.myRecyclerView.context,
            (binding.myRecyclerView.layoutManager as LinearLayoutManager).orientation
        )
        binding.myRecyclerView.addItemDecoration(dividerItemDecoration)
        return binding.root
    }

    override fun onItemClick(view: View?, position: Int) {
        Toast.makeText(context, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show()
    }
}