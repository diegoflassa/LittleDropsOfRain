package io.github.diegoflassa.littledropsofrain.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.adapters.MyRecyclerViewAdapter


class GalleryFragment : Fragment(), MyRecyclerViewAdapter.ItemClickListener {

    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var adapter: MyRecyclerViewAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProvider.NewInstanceFactory().create(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
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
        val recyclerView: RecyclerView = root.findViewById(R.id.my_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MyRecyclerViewAdapter(context, animalNames)
        adapter.setClickListener(this)
        recyclerView.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).getOrientation()
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
        return root
    }

    override fun onItemClick(view: View?, position: Int) {
        Toast.makeText(context, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}