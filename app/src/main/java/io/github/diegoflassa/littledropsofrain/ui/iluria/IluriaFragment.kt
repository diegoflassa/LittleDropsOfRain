package io.github.diegoflassa.littledropsofrain.ui.iluria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.databinding.FragmentIluriaBinding
import io.github.diegoflassa.littledropsofrain.helpers.viewLifecycle
import io.github.diegoflassa.littledropsofrain.models.IluriaViewModel
import io.github.diegoflassa.littledropsofrain.models.IluriaViewState

class IluriaFragment : Fragment() {

    companion object{
        fun newInstance() = IluriaFragment()
    }
    private val iluriaViewModel: IluriaViewModel by viewModels()
    private var binding: FragmentIluriaBinding by viewLifecycle()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIluriaBinding.inflate(inflater, container, false)
        iluriaViewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(it)
        })
        iluriaViewModel.viewState.text = binding.textIluria.text.toString()
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateUI(iluriaViewModel.viewState)
    }
    private fun updateUI(viewState: IluriaViewState) {
        // Update the UI
        binding.textIluria.text = viewState.text
    }

}