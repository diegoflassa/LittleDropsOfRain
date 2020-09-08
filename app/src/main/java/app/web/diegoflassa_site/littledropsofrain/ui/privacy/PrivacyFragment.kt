package app.web.diegoflassa_site.littledropsofrain.ui.privacy

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.databinding.FragmentPrivacyBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.viewLifecycle
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 * Use the [PrivacyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrivacyFragment : Fragment() {
    var binding: FragmentPrivacyBinding by viewLifecycle()
    private lateinit var toggle : ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrivacyBinding.inflate(inflater, container, false)

        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
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
        fab?.visibility = View.GONE

        binding.txtVwPrivacy.movementMethod = ScrollingMovementMethod()
        binding.txtVwPrivacy.text = HtmlCompat.fromHtml(getString(R.string.privacy_policy), HtmlCompat.FROM_HTML_MODE_COMPACT)
        return binding.root
    }

    override fun onDestroyView(){
        super.onDestroyView()
        if(this::toggle.isInitialized) {
            val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
            drawerLayout.removeDrawerListener(toggle)
        }
    }

    companion object {
        fun newInstance() = PrivacyFragment()
    }
}