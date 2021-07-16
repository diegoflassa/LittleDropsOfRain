/*
 * Copyright 2021 The Little Drops of Rain Project
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.user_profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.FilesDao
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnFileUploadedFailureListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnFileUploadedListener
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentUserProfileBinding
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.LoggedUser
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.isSafeToAccessViewModel
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.runOnUiThread
import app.web.diegoflassa_site.littledropsofrain.presentation.contracts.CropImageResultContract
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.viewLifecycle
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class UserProfileFragment :
    Fragment(),
    OnFileUploadedListener,
    OnFileUploadedFailureListener,
    ActivityResultCallback<Uri?> {

    companion object {
        fun newInstance() = UserProfileFragment()
    }

    private lateinit var toggle: ActionBarDrawerToggle

    val viewModel: UserProfileViewModel by stateViewModel()
    private var binding: FragmentUserProfileBinding by viewLifecycle()
    private lateinit var getContentLauncher: ActivityResultLauncher<String>
    private lateinit var cropImageLauncher: ActivityResultLauncher<Pair<Uri, Pair<Float, Float>>>
    private var isStopped = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
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
            if (drawerLayout != null)
                toggle.syncState()
            activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
        }

        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        binding.userBtnUpdate.isEnabled = false
        binding.userBtnChangeImage.isEnabled = false
        binding.userBtnChangeImage.setOnClickListener {
            getContentLauncher.launch("image/*")
        }
        binding.userBtnUpdate.setOnClickListener {
            if (LoggedUser.userLiveData.value != null) {
                LoggedUser.userLiveData.value!!.name = binding.userEdtTxtName.text.toString()
                LoggedUser.userLiveData.value!!.email = binding.userTxtVwEmail.text.toString()
                UserDao.insertOrUpdate(LoggedUser.userLiveData.value!!)
            }
            Toast.makeText(context, getString(R.string.saved), Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
        binding.userBtnExit.setOnClickListener {
            findNavController().navigateUp()
        }

        cropImageLauncher = registerForActivityResult(CropImageResultContract(), this)
        getContentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it == null) {
                if (LoggedUser.userLiveData.value != null) {
                    if (LoggedUser.userLiveData.value!!.imageUrl != null) {
                        FilesDao.remove(Uri.parse(LoggedUser.userLiveData.value!!.imageUrl))
                    }
                    LoggedUser.userLiveData.value!!.imageUrl = null
                }
                binding.userPicture.setImageDrawable(null)
            } else {
                val data = Pair(it, CropImageResultContract.ASPECT_RATIO_BOX)
                cropImageLauncher.launch(data)
            }
        }
        setCurrentUserToUI()
        return binding.root
    }

    override fun onPause() {
        viewModel.name = binding.userEdtTxtName.text.toString()
        viewModel.email = binding.userTxtVwEmail.text.toString()
        super.onPause()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        updateUI(viewModel)
    }

    override fun onStop() {
        isStopped = true
        super.onStop()
    }

    override fun onDestroyView() {
        if (this::toggle.isInitialized) {
            val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
            drawerLayout.removeDrawerListener(toggle)
        }
        super.onDestroyView()
    }

    override fun onResume() {
        isStopped = false
        super.onResume()
    }

    private fun updateUI(viewState: UserProfileViewModel) {
        binding.userEdtTxtName.setText(viewState.name)
        binding.userTxtVwEmail.text = viewState.email
        // Update the UI
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.GONE
    }

    private fun showLoadingScreen() {
        binding.userProfileProgress.visibility = View.VISIBLE
    }

    private fun hideLoadingScreen() {
        runOnUiThread {
            if (isSafeToAccessViewModel() && !isStopped) {
                binding.userProfileProgress.visibility = View.GONE
            }
        }
    }

    private fun setCurrentUserToUI() {
        val user = LoggedUser.userLiveData.value
        LoggedUser.userLiveData.value = user
        if (LoggedUser.userLiveData.value != null) {
            viewModel.name = LoggedUser.userLiveData.value?.name!!
            viewModel.email = LoggedUser.userLiveData.value?.email!!
            updateUI(viewModel)

            binding.userEdtTxtName.setText(LoggedUser.userLiveData.value!!.name)
            binding.userTxtVwEmail.text = LoggedUser.userLiveData.value!!.email
            binding.userPicture.load(LoggedUser.userLiveData.value!!.imageUrl) {
                placeholder(R.drawable.image_placeholder)
            }
            binding.userBtnChangeImage.isEnabled = true
            binding.userBtnUpdate.isEnabled = true
        }
    }

    override fun onActivityResult(uri: Uri?) {
        showLoadingScreen()
        FilesDao.insert(uri!!, this, this, true)
    }

    override fun onFileUploaded(local: Uri, remote: Uri) {
        if (LoggedUser.userLiveData.value != null) {
            if (LoggedUser.userLiveData.value!!.imageUrl != null) {
                FilesDao.remove(Uri.parse(LoggedUser.userLiveData.value!!.imageUrl))
            }
            LoggedUser.userLiveData.value!!.imageUrl = remote.toString()
            binding.userPicture.load(LoggedUser.userLiveData.value!!.imageUrl) {
                placeholder(R.drawable.image_placeholder)
            }
        }
        hideLoadingScreen()
    }

    override fun onFileUploadedFailure(file: Uri, exception: Exception?) {
        Toast.makeText(context, getString(R.string.file_upload_failure), Toast.LENGTH_LONG).show()
        hideLoadingScreen()
    }
}
