package io.github.diegoflassa.littledropsofrain.ui.reload_products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.TypiconsIcons
import io.github.diegoflassa.littledropsofrain.R
import io.github.diegoflassa.littledropsofrain.data.dao.ProductDao
import io.github.diegoflassa.littledropsofrain.databinding.FragmentReloadProductsBinding
import io.github.diegoflassa.littledropsofrain.helpers.Helper
import io.github.diegoflassa.littledropsofrain.helpers.viewLifecycle
import io.github.diegoflassa.littledropsofrain.models.ReloadProductsViewModel
import io.github.diegoflassa.littledropsofrain.models.ReloadProductsViewState
import io.github.diegoflassa.littledropsofrain.xml.ProductParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ReloadProductsFragment : Fragment(), ProductParser.OnParseProgress {

    private val viewModel: ReloadProductsViewModel by viewModels()
    private val ioScope = CoroutineScope(Dispatchers.IO)
    var binding : FragmentReloadProductsBinding by viewLifecycle()

    companion object {
        fun newInstance() = ReloadProductsFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReloadProductsBinding.inflate(inflater, container, false)
        binding.btnReloadProducts.icon = IconDrawable(requireContext(), TypiconsIcons.typcn_refresh)
        binding.btnReloadProducts.setOnClickListener {
            fetchProducts()
        }
        viewModel.viewState.observe(viewLifecycleOwner, {
            updateUI(it)
        })
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
        return inflater.inflate(R.layout.fragment_reload_products, container, false)
    }

    override fun onResume() {
        super.onResume()
        updateUI(viewModel.viewState)
    }

    private fun updateUI(viewState: ReloadProductsViewState) {
        // Update the UI
        viewState.text = ""
        binding.mlTxtVwProgress.text = viewModel.viewState.progress
    }

    private fun fetchProducts(){
        val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        scheduler.scheduleAtFixedRate({
            ioScope.launch {
                val productParser = ProductParser(this@ReloadProductsFragment)
                val products = productParser.parse()
                ProductDao.insertAll(Helper.iluriaProductToProduct(products))
            }
        }, 0, 12, TimeUnit.HOURS)
    }

    override fun onParseProgressChange(progress: String) {
        viewModel.viewState.progress+= progress
        updateUI(viewModel.viewState)
    }

}