package app.web.diegoflassa_site.littledropsofrain

import android.content.ComponentCallbacks2
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.databinding.ActivityMainBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.helpers.IntentHelper
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnKeyLongPressListener
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnUserFoundListener
import app.web.diegoflassa_site.littledropsofrain.models.MainActivityViewModel
import app.web.diegoflassa_site.littledropsofrain.models.MainActivityViewState
import app.web.diegoflassa_site.littledropsofrain.services.SetupProductsUpdateWorkerService
import app.web.diegoflassa_site.littledropsofrain.ui.send_message.SendMessageFragment
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import com.joanzapata.iconify.fonts.TypiconsIcons
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.nav_header_main.view.*


class MainActivity : AppCompatActivity(),
    OnUserFoundListener, ComponentCallbacks2 {

    private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        val TAG = MainActivity::class.simpleName
    }

    private val viewModel: MainActivityViewModel by viewModels()
    private var currentUser: User = User()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var fab: FloatingActionButton
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle : ActionBarDrawerToggle
    private var authenticateOnResume = false
    var mOnKeyLongPressListener : OnKeyLongPressListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel.viewState.observe(this, {
            updateUI(it)
        })
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab = findViewById(R.id.fab)
        fab.isEnabled= false
        fab.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(SendMessageFragment.ACTION_SEND_KEY, SendMessageFragment.ACTION_SEND)
            findNavController(R.id.nav_host_fragment).navigate(R.id.send_message_fragment, bundle)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
        drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
                //Log.i(TAG, "onDrawerStateChanged: $newState")
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                Log.i(TAG, "onDrawerSlide")
            }

            override fun onDrawerClosed(drawerView: View) {
                Log.i(TAG, "onDrawerClosed")
            }

            override fun onDrawerOpened(drawerView: View) {
                Log.i(TAG, "onDrawerOpened")
                val currentUser = mAuth.currentUser
                if (currentUser != null) {
                    Picasso.get().load(currentUser.photoUrl)
                        .placeholder(R.drawable.image_placeholder).into(
                            binding.navView.nav_vw_image
                        )
                    binding.navView.nav_vw_name.text = currentUser.displayName
                    binding.navView.nav_vw_email.text = currentUser.email
                } else {
                    binding.navView.nav_vw_image.setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.mipmap.ic_launcher_round
                        )
                    )
                    binding.navView.nav_vw_name.text = getString(R.string.not_logged_in)
                    binding.navView.nav_vw_email.text = ""
                }
            }
        })

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_facebook,
                R.id.nav_instagram,
                R.id.nav_messages,
                R.id.nav_all_messages
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        val bnv = findViewById<BottomNavigationView>(R.id.nav_bottom)
        val menu = bnv.menu
        menu.findItem(R.id.nav_all_messages).icon = IconDrawable(this, SimpleLineIconsIcons.icon_envelope_letter)
        menu.findItem(R.id.nav_reload_products).icon = IconDrawable(this, SimpleLineIconsIcons.icon_loop)
        menu.findItem(R.id.nav_send_topic_message).icon = IconDrawable(this, SimpleLineIconsIcons.icon_envelope)
        menu.findItem(R.id.nav_users).icon = IconDrawable(this, SimpleLineIconsIcons.icon_users)

        bnv.setupWithNavController(navController)
        bnv.visibility = View.GONE

        mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser==null){
            authenticateOnResume = true
            // Create and launch sign-in intent
        }else{
            Toast.makeText(
                applicationContext, getString(
                    R.string.user_logged_as,
                    mAuth.currentUser!!.displayName
                ), Toast.LENGTH_LONG
            ).show()
        }

        val firebaseUserLiveData = viewModel.getFirebaseAuthLiveData()
        firebaseUserLiveData.observe(this,
            { firebaseUser ->
                if (firebaseUser != null) {
                    fab.isEnabled= true
                    UserDao.findByEMail(firebaseUser.email, this)
                } else {
                    fab.isEnabled= false
                    currentUser = User()
                    setupDrawerMenuIntems()
                    val navOptions = NavOptions.Builder().setPopUpTo(R.id.nav_home, true).build()
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_home, null, navOptions)
                }
            })
        SetupProductsUpdateWorkerService.setupWorker(applicationContext)
    }

    /**
     * Release memory when the UI becomes hidden or when system resources become low.
     * @param level the memory-related event that was raised.
     */
    override fun onTrimMemory(level: Int) {

        // Determine which lifecycle or system event was raised.
        when (level) {

            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */
            }

            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */
            }

            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND,
            ComponentCallbacks2.TRIM_MEMORY_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */
            }

            else -> {
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
            }
        }
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        mOnKeyLongPressListener?.keyLongPress(keyCode, event)
        return super.onKeyLongPress(keyCode, event)
    }
    override fun onResume() {
        super.onResume()
        if(authenticateOnResume){
            authenticateOnResume= false
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_authentication)
        }
        updateUI(viewModel.viewState)
        handleIntent()
    }

    override fun onStop() {
        if(this::toggle.isInitialized) {
            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
            drawerLayout.removeDrawerListener(toggle)
        }
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "Main activity destroyed")
        super.onDestroy()
    }

    private fun handleIntent(){
        if(intent.extras!=null && intent.extras!!.containsKey(IntentHelper.EXTRA_START_WHAT)){
            when(intent.extras!!.get(IntentHelper.EXTRA_START_WHAT)){
                "privacy" -> {
                    findNavController(R.id.nav_host_fragment).navigate(NavMainDirections.actionGlobalPrivacyFragment())
                    intent.removeExtra(IntentHelper.EXTRA_START_WHAT)
                }
            }
        }
    }

    private fun updateUI(viewState: MainActivityViewState) {
        // Update he UI
        viewState.text = ""
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var ret= false
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                ret = true
            }
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment).navigate(NavMainDirections.actionGlobalSettingsFragment())
                ret = true
            }
            R.id.action_licenses -> {
                OssLicensesMenuActivity.setActivityTitle(getString(R.string.licenses))
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
                ret = true
            }
            R.id.action_privacy -> {
                findNavController(R.id.nav_host_fragment).navigate(NavMainDirections.actionGlobalPrivacyFragment())
                ret = true
            }
        }
        return ret
    }

    private fun setupDrawerMenuIntems() {
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val navHome = navView.menu.findItem(R.id.nav_home)
        navHome.icon = IconDrawable(this, SimpleLineIconsIcons.icon_home)
        val navFacebook = navView.menu.findItem(R.id.nav_facebook)
        navFacebook.icon = IconDrawable(this, SimpleLineIconsIcons.icon_social_facebook)
        val navInstagram = navView.menu.findItem(R.id.nav_instagram)
        navInstagram.icon = IconDrawable(this, TypiconsIcons.typcn_social_instagram)
        val navMessages = navView.menu.findItem(R.id.nav_messages)
        navMessages.icon = IconDrawable(this, SimpleLineIconsIcons.icon_envelope)
        val navAdmin = navView.menu.findItem(R.id.nav_all_messages)
        navAdmin.icon = IconDrawable(this, SimpleLineIconsIcons.icon_wrench)
        val navAuthentication = navView.menu.findItem(R.id.nav_authentication)
        if(mAuth.currentUser!=null) {
            navAuthentication.icon = IconDrawable(this, SimpleLineIconsIcons.icon_logout)
            navAuthentication.title = getString(R.string.logout)
        }else{
            navAuthentication.icon = IconDrawable(this, SimpleLineIconsIcons.icon_login)
            navAuthentication.title = getString(R.string.login)
        }
        if(mAuth.currentUser!=null){
            navMessages.isEnabled = true
            navMessages.isVisible = true
        }else{
            navMessages.isEnabled = false
            navMessages.isVisible = false
        }
        if(!currentUser.isAdmin) {
            navAdmin.isEnabled = false
            navAdmin.isVisible = false
        }else{
            navAdmin.isEnabled = true
            navAdmin.isVisible = true
        }
        navHome.isChecked = true
    }

    override fun onUserFound(user: User?) {
        if (user != null) {
            currentUser = user
            if(user.isAdmin){
                Helper.requestReadExternalStoragePermission(this)
            }
            UserDao.insert(currentUser)
        }else if(FirebaseAuth.getInstance().currentUser!=null) {
            val userFb = Helper.firebaseUserToUser(FirebaseAuth.getInstance().currentUser!!)
            UserDao.insert(userFb)
        }
        setupDrawerMenuIntems()
    }
}