package app.web.diegoflassa_site.littledropsofrain.ui.off_air

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentOffAirBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.isSafeToAccessViewModel
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import app.web.diegoflassa_site.littledropsofrain.models.OffAirViewModel
import app.web.diegoflassa_site.littledropsofrain.models.OffAirViewState
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings


class OffAirFragment : Fragment() {

    companion object {
        var REMOTE_CONFIG_OFF_AIR_MESSAGE_EN = "remote_config_off_air_message_en"
        var REMOTE_CONFIG_OFF_AIR_MESSAGE_PT = "remote_config_off_air_message_pt"
        var REMOTE_CONFIG_IS_OFF_AIR = "remote_config_is_off_air"
        fun newInstance() = OffAirFragment()
    }
    private var toggle: ActionBarDrawerToggle? = null
    private var isStopped: Boolean = false
    private val viewModel: OffAirViewModel by viewModels()
    private var binding: FragmentOffAirBinding by viewLifecycle()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOffAirBinding.inflate(inflater, container, false)

        binding.fabOffAir.setOnClickListener{
            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                REMOTE_CONFIG_OFF_AIR_MESSAGE_EN = binding.edtTxtMlOffAirMessageEn.text.toString()
                REMOTE_CONFIG_OFF_AIR_MESSAGE_PT = binding.edtTxtMlOffAirMessagePt.text.toString()
                REMOTE_CONFIG_IS_OFF_AIR = binding.chkBxOffAir.isChecked.toString()
            }
            remoteConfig.setConfigSettingsAsync(configSettings).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(requireContext(), getString(R.string.configuration_applied),
                        Toast.LENGTH_SHORT).show()
                }
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
            drawerLayout?.addDrawerListener(toggle!!)
            if (drawerLayout != null)
                toggle?.syncState()
            activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
        }
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        return binding.root
    }

    override fun onDestroyView() {
        removeToogleListener()
        super.onDestroyView()
    }

    override fun onStop() {
        isStopped = true
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(isSafeToAccessViewModel() && !isStopped) {
            viewModel.viewState.messageEn = binding.edtTxtMlOffAirMessageEn.text.toString()
            viewModel.viewState.messagePt = binding.edtTxtMlOffAirMessagePt.text.toString()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        updateUI(viewModel.viewState)
    }

    override fun onResume() {
        super.onResume()
        isStopped = false
        updateUI(viewModel.viewState)
    }

    private fun removeToogleListener() {
        if (toggle != null) {
            val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
            drawerLayout.removeDrawerListener(toggle!!)
            toggle = null
        }
    }

    private fun updateUI(viewState: OffAirViewState) {
        // Update the UI
        viewState.text = ""
        binding.edtTxtMlOffAirMessageEn.setText(viewState.messageEn)
        binding.edtTxtMlOffAirMessagePt.setText(viewState.messagePt)
    }
}