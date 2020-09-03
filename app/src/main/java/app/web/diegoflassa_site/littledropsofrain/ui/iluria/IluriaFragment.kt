package app.web.diegoflassa_site.littledropsofrain.ui.iluria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentIluriaBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.models.IluriaViewModel
import app.web.diegoflassa_site.littledropsofrain.models.IluriaViewState

class IluriaFragment : Fragment() {

    companion object{
        fun newInstance() = IluriaFragment()
    }
    private val viewModel: IluriaViewModel by viewModels()
    private var binding: FragmentIluriaBinding by viewLifecycle()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIluriaBinding.inflate(inflater, container, false)
        viewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(it)
        })
        viewModel.viewState.text = binding.textIluria.text.toString()
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateUI(viewModel.viewState)
    }
    private fun updateUI(viewState: IluriaViewState) {
        // Update the UI
        binding.textIluria.text = viewState.text
    }

}