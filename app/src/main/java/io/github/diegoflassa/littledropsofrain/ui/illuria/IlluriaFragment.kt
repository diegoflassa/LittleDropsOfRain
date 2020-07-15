package io.github.diegoflassa.littledropsofrain.ui.illuria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import io.github.diegoflassa.littledropsofrain.R

class IlluriaFragment : Fragment() {

    private lateinit var homeViewModel: IlluriaViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider.NewInstanceFactory().create(IlluriaViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_illuria, container, false)
        val textView: TextView = root.findViewById(R.id.text_illuria)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}