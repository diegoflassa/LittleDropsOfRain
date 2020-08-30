package io.github.diegoflassa.littledropsofrain.ui.reload_products

import android.os.Bundle
import android.text.Layout
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CompoundButton
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.dao.ProductDao
import io.github.diegoflassa.littledropsofrain.data.entities.Product
import io.github.diegoflassa.littledropsofrain.databinding.FragmentReloadProductsBinding
import io.github.diegoflassa.littledropsofrain.helpers.Helper
import io.github.diegoflassa.littledropsofrain.helpers.runOnUiThread
import io.github.diegoflassa.littledropsofrain.helpers.viewLifecycle
import io.github.diegoflassa.littledropsofrain.interfaces.OnProductInsertedListener
import io.github.diegoflassa.littledropsofrain.models.ReloadProductsViewModel
import io.github.diegoflassa.littledropsofrain.models.ReloadProductsViewState
import io.github.diegoflassa.littledropsofrain.xml.ProductParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ReloadProductsFragment : Fragment(), ProductParser.OnParseProgress,
    OnProductInsertedListener {

    private val viewModel: ReloadProductsViewModel by viewModels()
    var binding : FragmentReloadProductsBinding by viewLifecycle()
    private val ioScope = CoroutineScope(Dispatchers.IO)

    companion object {
        fun newInstance() = ReloadProductsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReloadProductsBinding.inflate(inflater, container, false)
        viewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(it)
        })

        binding.mlTxtVwProgress.movementMethod = ScrollingMovementMethod()
        binding.chkbxRemoveNotFoundProducts.isChecked = true
        binding.chkbxRemoveNotFoundProducts.setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
            viewModel.viewState.removeNotFoundProducts = checked
        }
        binding.btnReloadProducts.setOnClickListener {
            fetchProducts()
        }
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.setNavigationOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            val toggle = ActionBarDrawerToggle(
                activity,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerLayout?.addDrawerListener(toggle)
            toggle.syncState()
            activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateUI(viewModel.viewState)
    }

    private fun updateUI(viewState: ReloadProductsViewState) {
        runOnUiThread {
            // Update the UI
            binding.mlTxtVwProgress.text  = viewModel.viewState.progress.toString()
            binding.chkbxRemoveNotFoundProducts.isChecked = viewState.removeNotFoundProducts
        }
    }

    private fun setTextAndScroll(text: String) {
        binding.mlTxtVwProgress.text = text

        var layout: Layout?
        val vto: ViewTreeObserver = binding.mlTxtVwProgress.viewTreeObserver
        vto.addOnGlobalLayoutListener {
            layout = binding.mlTxtVwProgress.layout
            val scrollDelta: Int = ((layout?.getLineBottom(binding.mlTxtVwProgress.lineCount - 1)
                ?.minus(binding.mlTxtVwProgress.scrollY) ?: 0 ) - binding.mlTxtVwProgress.height)
            if (scrollDelta > 0) binding.mlTxtVwProgress.scrollBy(0, scrollDelta)
        }
    }

    private fun fetchProducts() {
        ioScope.launch {
            val productParser = ProductParser(this@ReloadProductsFragment)
            val products = productParser.parse()
            viewModel.viewState.progress.append("Uploading products to Firebase" + System.lineSeparator())
            ProductDao.insertAll(Helper.iluriaProductToProduct(products), binding.chkbxRemoveNotFoundProducts.isChecked, this@ReloadProductsFragment)
        }
    }

    override fun onParseProgressChange(progress: String) {
        viewModel.viewState.progress.append(progress + System.lineSeparator())
        updateUI(viewModel.viewState)
    }

    override fun onProductInserted(product: Product) {
        viewModel.viewState.progress.append("Product ${product.uid} - ${product.title} inserted." + System.lineSeparator())
        updateUI(viewModel.viewState)
    }

}