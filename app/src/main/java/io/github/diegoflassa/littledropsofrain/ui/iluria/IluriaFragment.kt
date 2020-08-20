package io.github.diegoflassa.littledropsofrain.ui.iluria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.github.diegoflassa.littledropsofrain.databinding.FragmentIluriaBinding
import io.github.diegoflassa.littledropsofrain.models.IluriaViewModel
import viewLifecycle

class IluriaFragment : Fragment() {

    companion object{
        fun newInstance() = IluriaFragment()
    }
    private val homeViewModel: IluriaViewModel by viewModels()
    private var binding: FragmentIluriaBinding by viewLifecycle()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIluriaBinding.inflate(inflater, container, false)
        homeViewModel.text.observe(viewLifecycleOwner, {
            binding.textIluria.text = it
        })
        return binding.root
    }

}