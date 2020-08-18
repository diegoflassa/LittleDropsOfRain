package io.github.diegoflassa.littledropsofrain.ui.iluria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.github.diegoflassa.littledropsofrain.databinding.FragmentIluriaBinding

class IluriaFragment : Fragment() {

    companion object{
        fun newInstance() = IluriaFragment()
    }
    private lateinit var homeViewModel: IluriaViewModel
    private lateinit var binding: FragmentIluriaBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIluriaBinding.inflate(inflater, container, false)
        homeViewModel =
            ViewModelProvider.NewInstanceFactory().create(IluriaViewModel::class.java)
        homeViewModel.text.observe(viewLifecycleOwner, {
            binding.textIluria.text = it
        })
        return binding.root
    }
}