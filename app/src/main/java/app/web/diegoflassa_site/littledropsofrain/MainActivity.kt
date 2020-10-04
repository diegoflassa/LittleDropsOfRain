package app.web.diegoflassa_site.littledropsofrain

import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import app.web.diegoflassa_site.littledropsofrain.contracts.EmailLinkAuthActivityResultContract
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.databinding.ActivityMainBinding
import app.web.diegoflassa_site.littledropsofrain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.helpers.IntentHelper
import app.web.diegoflassa_site.littledropsofrain.helpers.LoggedUser
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnKeyLongPressListener
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnUserFoundListener
import app.web.diegoflassa_site.littledropsofrain.models.MainActivityViewModel
import app.web.diegoflassa_site.littledropsofrain.models.MainActivityViewState
import app.web.diegoflassa_site.littledropsofrain.services.SetupProductsUpdateWorkerService
import app.web.diegoflassa_site.littledropsofrain.ui.SettingsFragment
import app.web.diegoflassa_site.littledropsofrain.ui.send_message.SendMessageFragment
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import com.joanzapata.iconify.fonts.TypiconsIcons
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.util.*


class MainActivity : AppCompatActivity(),
    OnUserFoundListener, ComponentCallbacks2, ActivityResultCallback<Int> {

    private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        val TAG = MainActivity::class.simpleName
    }

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var fab: FloatingActionButton
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private var isSetUpUserInDrawer = false
    private var lastIntent: Intent? = null
    var mOnKeyLongPressListener: OnKeyLongPressListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel.viewState.observe(this, {
            updateUI(it)
        })
        // Initialize the singleton class
        LoggedUser.userLiveData.value = null
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab = findViewById(R.id.fab)
        fab.isEnabled = false
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
                if (!isSetUpUserInDrawer) {
                    setUpUserInDrawer()
                    isSetUpUserInDrawer = true
                }
                Log.i(TAG, "onDrawerOpened")
            }
        })

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
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
        menu.findItem(R.id.nav_all_messages).icon = IconDrawable(
            this,
            SimpleLineIconsIcons.icon_envelope_letter
        )
        menu.findItem(R.id.nav_reload_products).icon = IconDrawable(
            this,
            SimpleLineIconsIcons.icon_loop
        )
        menu.findItem(R.id.nav_send_topic_message).icon = IconDrawable(
            this,
            SimpleLineIconsIcons.icon_envelope
        )
        menu.findItem(R.id.nav_users).icon = IconDrawable(this, SimpleLineIconsIcons.icon_users)

        bnv.setupWithNavController(navController)
        bnv.visibility = View.GONE

        FirebaseAuth.getInstance().addAuthStateListener { authData ->
            val firebaseUser = authData.currentUser
            if (firebaseUser != null) {
                fab.isEnabled = true
                firebaseUser.getIdToken(true).addOnCompleteListener {
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putString(
                        SettingsFragment.LOGGED_USER_EMAIL_KEY, firebaseUser.email
                    ).apply()
                }
                UserDao.findByEMail(firebaseUser.email, this)
            }
        }
        signInUser()
        handleIntent()
    }

    private fun signInUser() {
        val email = PreferenceManager.getDefaultSharedPreferences(this).getString(
            SettingsFragment.LOGGED_USER_EMAIL_KEY, ""
        )
        if (!email.isNullOrEmpty()) {
            UserDao.findByEMail(email, this)
        } else {
            // Navigate to the sign-in/authentication fragment
            findNavController(R.id.nav_host_fragment).navigate(MainActivityDirections.actionGlobalAuthenticationFragment())
        }
    }

    private fun setUpUserInDrawer() {
        if (LoggedUser.userLiveData.value != null) {
            if (LoggedUser.userLiveData.value!!.imageUrl != null) {
                Picasso.get().load(LoggedUser.userLiveData.value!!.imageUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .error(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.mipmap.ic_launcher_round
                        )!!
                    ).into(
                        binding.navView.nav_vw_image
                    )
            } else {
                binding.navView.nav_vw_image.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.mipmap.ic_launcher_round
                    )
                )
            }
            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
            binding.navView.nav_vw_image.setOnClickListener {
                drawerLayout.close()
                findNavController(R.id.nav_host_fragment).navigate(MainActivityDirections.actionGlobalUserProfileFragment())
            }
            binding.navView.nav_vw_name.text = LoggedUser.userLiveData.value!!.name
            binding.navView.nav_vw_email.text = LoggedUser.userLiveData.value!!.email

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

    @Suppress("DEPRECATION")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val subscribedLanguage = PreferenceManager.getDefaultSharedPreferences(this).getString(
            SettingsFragment.SUBSCRIBED_LANGUAGE_KEY, ""
        )
        val sll = Locale(subscribedLanguage!!)

        val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0)
        } else {
            resources.configuration.locale
        }

        if (currentLocale.language != sll.language) {
            val oldTopicNews = Helper.getTopicNewsForLocale(this, sll)
            Helper.unsubscribeToNews(this, oldTopicNews)
            Helper.subscribeToNews(this)

            val oldTopicPromos = Helper.getTopicPromosForLocale(this, sll)
            Helper.unsubscribeToPromos(this, oldTopicPromos)
            Helper.subscribeToPromos(this)

            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(
                SettingsFragment.SUBSCRIBED_LANGUAGE_KEY, currentLocale.language
            ).apply()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        mOnKeyLongPressListener?.keyLongPress(keyCode, event)
        return super.onKeyLongPress(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        updateUI(viewModel.viewState)
        handleIntent()
    }

    override fun onStop() {
        if (this::toggle.isInitialized) {
            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
            drawerLayout.removeDrawerListener(toggle)
        }
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "Main activity destroyed")
        super.onDestroy()
    }

    private fun handleIntent() {
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (lastIntent != intent) {
            if (intent != null) {
                if (intent.extras != null && intent.extras!!.containsKey(IntentHelper.EXTRA_START_WHAT)) {
                    when (intent.extras!!.get(IntentHelper.EXTRA_START_WHAT)) {
                        "privacy" -> {
                            findNavController(R.id.nav_host_fragment).navigate(NavMainDirections.actionGlobalPrivacyFragment())
                            intent.removeExtra(IntentHelper.EXTRA_START_WHAT)
                        }
                        "tos" -> {
                            findNavController(R.id.nav_host_fragment).navigate(NavMainDirections.actionGlobalTosFragment())
                            intent.removeExtra(IntentHelper.EXTRA_START_WHAT)
                        }
                        "licenses" -> {
                            showLicenses()
                        }
                    }
                }
                if (AuthUI.canHandleIntent(intent)) {
                    if (intent.extras == null) {
                        return
                    }
                    registerForActivityResult(
                        EmailLinkAuthActivityResultContract(),
                        this
                    ).launch(intent)
                }
            }
            lastIntent = intent
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
        var ret = false
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
                showLicenses()
                ret = true
            }
            R.id.action_privacy -> {
                findNavController(R.id.nav_host_fragment).navigate(NavMainDirections.actionGlobalPrivacyFragment())
                ret = true
            }
            R.id.action_tos -> {
                findNavController(R.id.nav_host_fragment).navigate(NavMainDirections.actionGlobalTosFragment())
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
        if (LoggedUser.userLiveData.value != null) {
            navAuthentication.icon = IconDrawable(this, SimpleLineIconsIcons.icon_logout)
            navAuthentication.title = getString(R.string.logout)
        } else {
            navAuthentication.icon = IconDrawable(this, SimpleLineIconsIcons.icon_login)
            navAuthentication.title = getString(R.string.login)
        }
        if (LoggedUser.userLiveData.value != null) {
            navMessages.isEnabled = true
            navMessages.isVisible = true
        } else {
            navMessages.isEnabled = false
            navMessages.isVisible = false
        }
        if (LoggedUser.userLiveData.value != null && LoggedUser.userLiveData.value!!.isAdmin) {
            navAdmin.isEnabled = true
            navAdmin.isVisible = true
        } else {
            navAdmin.isEnabled = false
            navAdmin.isVisible = false
        }
        navHome.isChecked = true
    }

    override fun onUserFound(user: User?) {
        when {
            user != null -> {
                if (user.isAdmin) {
                    Helper.requestReadExternalStoragePermission(this)
                    SetupProductsUpdateWorkerService.setupWorker(applicationContext)
                }
                Toast.makeText(
                    applicationContext, getString(
                        R.string.user_logged_as,
                        LoggedUser.userLiveData.value!!.name
                    ), Toast.LENGTH_LONG
                ).show()
            }
            FirebaseAuth.getInstance().currentUser != null -> {
                val userFb = Helper.firebaseUserToUser(FirebaseAuth.getInstance().currentUser!!)
                UserDao.insert(userFb)
                LoggedUser.userLiveData.value = userFb
                Firebase.auth.fetchSignInMethodsForEmail(LoggedUser.userLiveData.value!!.email!!)
                    .addOnSuccessListener { result ->
                        Toast.makeText(
                            applicationContext, getString(
                                R.string.user_logged_as,
                                LoggedUser.userLiveData.value!!.name
                            ), Toast.LENGTH_LONG
                        ).show()
                        val signInMethods = result.signInMethods!!
                        if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD) || signInMethods.contains(
                                EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD
                            )
                        ) {
                            findNavController(R.id.nav_host_fragment).navigate(
                                MainActivityDirections.actionGlobalUserProfileFragment()
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error getting sign in methods for user", exception)
                    }
            }
            else -> {
                // Navigate to the sign-in/authentication fragment
                findNavController(R.id.nav_host_fragment).navigate(MainActivityDirections.actionGlobalAuthenticationFragment())
            }
        }
        setupDrawerMenuIntems()
    }

    override fun onActivityResult(result: Int) {
        if (result == Activity.RESULT_OK) {
            // Successfully signed in
            //val user = FirebaseAuth.getInstance().currentUser
            // ...
        } else {
            Log.d(TAG, "Error signing in")
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    private fun showLicenses() {
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.licenses))
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
    }
}