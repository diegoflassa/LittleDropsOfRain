package app.web.diegoflassa_site.littledropsofrain.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.preferences.MyOnSharedPreferenceChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView


class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var mpcl : SharedPreferences.OnSharedPreferenceChangeListener
    private var toggle : ActionBarDrawerToggle? = null
    companion object{
        val TAG = SettingsFragment::class.simpleName
        fun newInstance(): Fragment {
            return SettingsFragment()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        registerPreferencesListener()
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        updateUI()
    }

    private fun updateUI(){
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE
        val bnv = activity?.findViewById<BottomNavigationView>(R.id.nav_bottom)
        bnv?.visibility = View.GONE
    }

    override fun onResume(){
        super.onResume()
        removeToogleListener()
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
            if(drawerLayout!=null)
                toggle?.syncState()
            activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
        }
    }

    override fun onDestroyView(){
        removeToogleListener()
        super.onDestroyView()
    }

    override fun onDestroy() {
        unregisterPreferencesListener()
        super.onDestroy()
    }

    private fun removeToogleListener(){
        if(toggle!=null) {
            val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)
            drawerLayout.removeDrawerListener(toggle!!)
            toggle = null
        }
    }

    private fun registerPreferencesListener() {
        mpcl = MyOnSharedPreferenceChangeListener(requireContext())
        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(mpcl)
    }

    private fun unregisterPreferencesListener() {
        PreferenceManager.getDefaultSharedPreferences(requireContext()).unregisterOnSharedPreferenceChangeListener(mpcl)
    }
}