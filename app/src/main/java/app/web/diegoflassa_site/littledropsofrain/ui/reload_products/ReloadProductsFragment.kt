package app.web.diegoflassa_site.littledropsofrain.ui.reload_products

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.work.*
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentReloadProductsBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.runOnUiThread
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.models.ReloadProductsViewModel
import app.web.diegoflassa_site.littledropsofrain.models.ReloadProductsViewState
import app.web.diegoflassa_site.littledropsofrain.workers.UpdateProductsWork
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import java.util.concurrent.TimeUnit


class ReloadProductsFragment : Fragment() {

    private var wasShowed: Boolean = false
    private lateinit var observer: Observer<WorkInfo>
    private var isStopped: Boolean = false
    private val viewModel: ReloadProductsViewModel by viewModels()
    var binding : FragmentReloadProductsBinding by viewLifecycle()
    private lateinit var toggle : ActionBarDrawerToggle

    companion object {
        fun newInstance() = ReloadProductsFragment()
        private var worker : OneTimeWorkRequest? = null
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
            if(worker==null) {
                reloadProducts()
            }else{
                cancel()
            }
        }
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.setNavigationOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            toggle = ActionBarDrawerToggle(
                activity,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerLayout?.addDrawerListener(toggle)
            if(drawerLayout!=null)
                toggle.syncState()
            activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
        }
        observer = Observer<WorkInfo> {
            if (it != null) {
                when (it.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        Toast.makeText(requireContext(), getString(R.string.finished), Toast.LENGTH_LONG).show()
                        cancel()
                    }

                    WorkInfo.State.RUNNING -> {
                        val progress = it.progress
                        val value = progress.getString(UpdateProductsWork.KEY_PROGRESS)
                        if (value != null) {
                            viewModel.viewState.progress.append(value + System.lineSeparator())
                        }
                        val productId = progress.getString(UpdateProductsWork.KEY_PRODUCT)
                        if (productId != null) {
                            viewModel.viewState.progress.append("Inserting product $productId" + System.lineSeparator())
                        }
                        val products = progress.getString(UpdateProductsWork.KEY_PRODUCTS)
                        if (products != null) {
                            viewModel.viewState.progress.append("Successfully inserted all $products products" + System.lineSeparator())
                            hideLoadingScreen()
                        }
                        updateUI(viewModel.viewState)
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        }
        if(worker!=null) {
            WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(worker!!.id).observe(viewLifecycleOwner, observer)
            reloadProducts(false)
            showLoadingScreen()
        }else {
            hideLoadingScreen()
        }
        return binding.root
    }

    override fun onDestroyView(){
        super.onDestroyView()
        if(this::toggle.isInitialized) {
            val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
            drawerLayout.removeDrawerListener(toggle)
        }
    }

    private fun reloadProducts(executeFetch : Boolean = true){
        if(executeFetch && !wasShowed) {
            wasShowed = true
            Toast.makeText(requireContext(), getString(R.string.be_patient), Toast.LENGTH_LONG).show()
        }
        if(executeFetch)
            fetchProducts()
        binding.btnReloadProducts.text = getString(R.string.cancel)
        binding.btnReloadProducts.icon = IconDrawable(requireContext(), SimpleLineIconsIcons.icon_close)
    }

    private fun cancel(){
        if(worker!=null) {
            WorkManager.getInstance(requireContext()).cancelWorkById(worker!!.id)
            worker = null
        }
        viewModel.viewState.progress.clear()
        binding.btnReloadProducts.text = getString(R.string.reload_products)
        binding.btnReloadProducts.icon = IconDrawable(requireContext(), SimpleLineIconsIcons.icon_refresh)
        hideLoadingScreen()
        updateUI(viewModel.viewState)
    }

    override fun onStop() {
        super.onStop()
        if(worker!=null) {
            WorkManager.getInstance(requireContext()).cancelWorkById(worker!!.id)
        }
        isStopped = true
    }
    override fun onResume() {
        super.onResume()
        updateUI(viewModel.viewState)
    }

    private fun showLoadingScreen(){
        binding.reloadProgress.visibility = View.VISIBLE
    }

    private fun hideLoadingScreen(){
        binding.reloadProgress.visibility = View.GONE
        binding.reloadProgress.invalidate()
    }

    private fun updateUI(viewState: ReloadProductsViewState) {
        if (!isRemoving && !isDetached && isAdded && !isStopped) {
            runOnUiThread {
                // Update the UI
                binding.mlTxtVwProgress.text = viewModel.viewState.progress.toString()
                binding.mlTxtVwProgress.invalidate()
                binding.chkbxRemoveNotFoundProducts.isChecked = viewState.removeNotFoundProducts
                binding.mlTxtVwProgress.post {
                    val scrollAmount =
                        binding.mlTxtVwProgress.layout.getLineTop(binding.mlTxtVwProgress.lineCount) - binding.mlTxtVwProgress.height
                    binding.mlTxtVwProgress.scrollTo(0, scrollAmount)
                }
            }
        }
    }

    private fun fetchProducts() {
        showLoadingScreen()
        worker = setupWorker()
        WorkManager.getInstance(requireContext()).enqueue(worker!!)
        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(worker!!.id).observe(viewLifecycleOwner, observer)
    }

    private fun setupWorker() : OneTimeWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        val removeNotFound = workDataOf(UpdateProductsWork.KEY_IN_REMOVE_NOT_FOUND to viewModel.viewState.removeNotFoundProducts)
        return OneTimeWorkRequest.Builder(UpdateProductsWork::class.java)
            .setConstraints(constraints)
            .setInputData(removeNotFound)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
    }
}