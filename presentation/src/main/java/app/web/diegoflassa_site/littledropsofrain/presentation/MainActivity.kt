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

package app.web.diegoflassa_site.littledropsofrain.presentation

import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
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
import app.web.diegoflassa_site.littledropsofrain.NavMainDirections
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnUserFoundListener
import app.web.diegoflassa_site.littledropsofrain.databinding.ActivityMainBinding
import app.web.diegoflassa_site.littledropsofrain.databinding.NavHeaderMainBinding
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.LoggedUser
import app.web.diegoflassa_site.littledropsofrain.domain.helpers.MainActivityHolder
import app.web.diegoflassa_site.littledropsofrain.domain.preferences.MyOnSharedPreferenceChangeListener
import app.web.diegoflassa_site.littledropsofrain.domain.services.NewMessagesService
import app.web.diegoflassa_site.littledropsofrain.domain.services.SetupProductsUpdateWorkerService
import app.web.diegoflassa_site.littledropsofrain.presentation.contracts.EmailLinkAuthActivityResultContract
import app.web.diegoflassa_site.littledropsofrain.presentation.helper.IntentHelper
import app.web.diegoflassa_site.littledropsofrain.presentation.interfaces.OnKeyLongPressListener
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.MainActivityViewModel
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.MainActivityViewState
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.SettingsFragment
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.send_message.SendMessageFragment
import coil.load
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.SimpleLineIconsIcons
import com.joanzapata.iconify.fonts.TypiconsIcons
import org.koin.androidx.viewmodel.ext.android.stateViewModel

// @AndroidEntryPoint
class MainActivity :
    AppCompatActivity(),
    OnUserFoundListener,
    ComponentCallbacks2,
    ActivityResultCallback<Int> {

    private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        private val TAG = MainActivity::class.simpleName
    }

    init {
        MainActivityHolder.mainActivityClass = MainActivity::class
    }

    private val viewModel: MainActivityViewModel by stateViewModel()
    private lateinit var fab: FloatingActionButton
    private var binding: ActivityMainBinding? = null
    private var bindingNavHeader: NavHeaderMainBinding? = null
    private lateinit var toggle: ActionBarDrawerToggle
    private var authenticateOnResume = false
    private var isSetUpUserInDrawer = false
    private var lastIntent: Intent? = null
    var mOnKeyLongPressListener: OnKeyLongPressListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val headerView = binding?.navView?.getHeaderView(0)
        bindingNavHeader = NavHeaderMainBinding.bind(headerView!!)
        viewModel.viewStateLiveData.observe(this) {
            updateUI(it)
        }
        // Initialize the singleton class
        LoggedUser.userLiveData.value = null
        setContentView(binding?.root)

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
                // Log.i(TAG, "onDrawerStateChanged: $newState")
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                Log.i(TAG, "onDrawerSlide")
            }

            override fun onDrawerClosed(drawerView: View) {
                Log.i(TAG, "onDrawerClosed")
            }

            override fun onDrawerOpened(drawerView: View) {
                if (!isSetUpUserInDrawer) {
                    Log.i(TAG, "Setting up user in drawer")
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
        binding?.navView?.setupWithNavController(navController)
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
        menu.findItem(R.id.nav_off_air).icon = IconDrawable(this, SimpleLineIconsIcons.icon_wrench)

        bnv.setupWithNavController(navController)
        bnv.visibility = View.GONE

        LoggedUser.userLiveData.observe(this) {
            if (it != null) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString(
                    SettingsFragment.LOGGED_USER_EMAIL_KEY, it.email
                ).apply()
            }
            fab.isEnabled = (it != null)
            isSetUpUserInDrawer = false
            setupDrawerMenuIntems()
        }

        FirebaseAuth.getInstance().addAuthStateListener { authData ->
            val firebaseUser = authData.currentUser
            var emailUser = ""
            if (firebaseUser != null) {
                emailUser = firebaseUser.email!!
            }
            UserDao.findByEMail(emailUser, this)
        }
        handleIntent()
    }

    private fun setUpUserInDrawer() {
        if (bindingNavHeader == null && bindingNavHeader?.navVwName == null) {
            Log.i(TAG, "bindingNavHeader is null")
        } else {
            Log.i(TAG, "bindingNavHeader is NOT null ${bindingNavHeader?.navVwName?.text}")
        }
        if (LoggedUser.userLiveData.value != null) {
            if (LoggedUser.userLiveData.value!!.imageUrl != null) {
                Log.i(TAG, "Setting up user image")
                bindingNavHeader?.navVwImage?.load(LoggedUser.userLiveData.value!!.imageUrl) {
                    placeholder(R.drawable.image_placeholder)
                    error(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.mipmap.ic_launcher_round
                        )
                    )
                }
            } else {
                bindingNavHeader?.navVwImage?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.mipmap.ic_launcher_round
                    )
                )
            }
            Log.i(TAG, "Setting user name and email")
            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
            bindingNavHeader?.navVwImage?.setOnClickListener {
                drawerLayout.close()
                findNavController(R.id.nav_host_fragment).navigate(MainActivityDirections.actionGlobalUserProfileFragment())
            }
            bindingNavHeader?.navVwName?.setText(LoggedUser.userLiveData.value!!.name)
            Log.i(TAG, "Name: ${LoggedUser.userLiveData.value!!.name}")
            bindingNavHeader?.navVwEmail?.setText(LoggedUser.userLiveData.value!!.email)
            Log.i(TAG, "EMail: ${LoggedUser.userLiveData.value!!.email}")
        } else {
            Log.i(TAG, "No user found!")
            bindingNavHeader?.navVwImage?.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.mipmap.ic_launcher_round
                )
            )
            bindingNavHeader?.navVwName?.setText(getString(R.string.not_logged_in))
            bindingNavHeader?.navVwEmail?.setText("")
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
        if (authenticateOnResume) {
            authenticateOnResume = false
            // Navigate to the sign-in/authentication fragment
            findNavController(R.id.nav_host_fragment).navigate(MainActivityDirections.actionGlobalAuthenticationFragment())
        }
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
        binding = null
        bindingNavHeader = null
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
                runEmailLinkAuth(intent)
            }
            lastIntent = intent
        }
    }

    private fun runEmailLinkAuth(intent: Intent?) {
        if (intent != null && AuthUI.canHandleIntent(intent)) {
            authenticateOnResume = false
            if (intent.extras == null) {
                return
            }
            registerForActivityResult(
                EmailLinkAuthActivityResultContract(),
                this
            ).launch(intent)
        }
    }

    private fun updateUI(viewState: MainActivityViewState) {
        // Update he UI
        viewState.text = ""
        isSetUpUserInDrawer = false
        setupDrawerMenuIntems()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        //val shoppingCart = menu.findItem(R.id.action_shopping_cart)
        //shoppingCart.icon = IconDrawable(this, SimpleLineIconsIcons.icon_bag)
        //shoppingCart.isVisible = false
        // shoppingCart.icon.setTint(Color.BLACK)
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
        val navMyLikedProducts = navView.menu.findItem(R.id.nav_my_liked_products)
        navMyLikedProducts.icon = IconDrawable(this, SimpleLineIconsIcons.icon_heart)
        val navAllProductsProducts = navView.menu.findItem(R.id.nav_all_products)
        navAllProductsProducts.icon = IconDrawable(this, SimpleLineIconsIcons.icon_present)
        if (LoggedUser.userLiveData.value != null) {
            navAuthentication.icon = IconDrawable(this, SimpleLineIconsIcons.icon_logout)
            navAuthentication.title = getString(R.string.logout)
        } else {
            navAuthentication.icon = IconDrawable(this, SimpleLineIconsIcons.icon_login)
            navAuthentication.title = getString(R.string.login)
        }
        if (false && LoggedUser.userLiveData.value != null) {
            navMessages.isEnabled = true
            navMessages.isVisible = true
            navMyLikedProducts.isEnabled = true
            navMyLikedProducts.isVisible = true
        } else {
            navMessages.isEnabled = false
            navMessages.isVisible = false
        }
        if (false && LoggedUser.userLiveData.value != null && LoggedUser.userLiveData.value!!.isAdmin) {
            subscribeToAdminMessages()
            navAdmin.isEnabled = true
            navAdmin.isVisible = true
            navAllProductsProducts.isEnabled = true
            navAllProductsProducts.isVisible = true
        } else {
            navAdmin.isEnabled = false
            navAdmin.isVisible = false
            navAllProductsProducts.isEnabled = false
            navAllProductsProducts.isVisible = false
        }
        // TODO Remove after returning menu options
        //val toolbar: Toolbar = findViewById(R.id.toolbar)
        //val shoppingCart = toolbar.menu.findItem(R.id.action_shopping_cart)
        if (LoggedUser.userLiveData.value != null && LoggedUser.userLiveData.value!!.isAdmin) {
            subscribeToAdminMessages()
            //if (shoppingCart != null) {
            //    shoppingCart.isVisible = true
            //}
            navAdmin.isEnabled = true
            navAdmin.isVisible = true
            navMyLikedProducts.isEnabled = true
            navMyLikedProducts.isVisible = true
            navAllProductsProducts.isEnabled = true
            navAllProductsProducts.isVisible = true
        } else {
            //if (shoppingCart != null) {
            //    shoppingCart.isVisible = false
            //}
            navAdmin.isEnabled = false
            navAdmin.isVisible = false
            navMyLikedProducts.isEnabled = false
            navMyLikedProducts.isVisible = false
            navAllProductsProducts.isEnabled = false
            navAllProductsProducts.isVisible = false
        }
        navMyLikedProducts.isEnabled = false
        navMyLikedProducts.isVisible = false
        navAllProductsProducts.isEnabled = false
        navAllProductsProducts.isVisible = false
        navHome.isChecked = true
    }

    private fun showAdminOptions() {
    }

    private fun hideAdminOptions() {
    }

    private fun subscribeToAdminMessages() {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sp.getBoolean(
                MyOnSharedPreferenceChangeListener.SP_KEY_SUBSCRIBE_ADMIN_MESSAGES,
                true
            )
        ) {
            val topic = Helper.getTopicAdminMessages(this)
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    var msg = getString(R.string.msg_subscribed_to_admins)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_subscribe_admins_failed)
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.d(TAG, "Not registered to receive promos")
        }
    }

    override fun onUserFound(user: User?) {
        when {
            user != null -> {
                if (FirebaseAuth.getInstance().currentUser!!.providerData.size > 1) {
                    user.providerId =
                        FirebaseAuth.getInstance().currentUser!!.providerData[1].providerId
                }
                user.lastSeen = Timestamp.now()
                UserDao.insertOrUpdate(user)
                if (user.isAdmin) {
                    Helper.requestReadExternalStoragePermission(this)
                    val jobScheduler =
                        getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                    val jobInfoProductsWorker = JobInfo.Builder(
                        SetupProductsUpdateWorkerService.JOB_ID,
                        ComponentName(this, SetupProductsUpdateWorkerService::class.java)
                    )
                    val jobInfoNewMessages = JobInfo.Builder(
                        NewMessagesService.JOB_ID,
                        ComponentName(
                            this,
                            NewMessagesService::class.java
                        )
                    )
                    var job = jobInfoProductsWorker.setRequiresCharging(false)
                        .setMinimumLatency(1)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                        // .setPeriodic(86400000)
                        .setOverrideDeadline((3 * 60 * 1000).toLong())
                        .build()

                    jobScheduler.schedule(job)
                    job = jobInfoNewMessages.setRequiresCharging(false)
                        // .setMinimumLatency(1)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPeriodic(30000)
                        // .setOverrideDeadline(3 * 60 * 1000)
                        .build()
                    jobScheduler.schedule(job)
                }
                Helper.requestGetCoarseLocationPermission(this)
                LoggedUser.userLiveData.value = user
                Toast.makeText(
                    applicationContext,
                    getString(
                        R.string.user_logged_as,
                        LoggedUser.userLiveData.value!!.name
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
            FirebaseAuth.getInstance().currentUser != null -> {
                val userFromFb =
                    Helper.firebaseUserToUser(FirebaseAuth.getInstance().currentUser!!)
                UserDao.insertOrUpdate(userFromFb)
                LoggedUser.userLiveData.value = userFromFb
                findNavController(R.id.nav_host_fragment).navigate(
                    MainActivityDirections.actionGlobalUserProfileFragment()
                )
                Toast.makeText(
                    applicationContext,
                    getString(
                        R.string.user_logged_as,
                        LoggedUser.userLiveData.value!!.name
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {
                authenticateOnResume = true
            }
        }
        isSetUpUserInDrawer = false
        setupDrawerMenuIntems()
    }

    override fun onActivityResult(result: Int) {
        if (result == Activity.RESULT_OK) {
            // Successfully signed in
            // val user = FirebaseAuth.getInstance().currentUser
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
