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
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.preference.PreferenceManager
import app.web.diegoflassa_site.littledropsofrain.BuildConfig
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.data.old.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.old.entities.User
import app.web.diegoflassa_site.littledropsofrain.data.old.interfaces.OnUserFoundListener
import app.web.diegoflassa_site.littledropsofrain.domain.old.helpers.Helper
import app.web.diegoflassa_site.littledropsofrain.domain.old.helpers.LoggedUser
import app.web.diegoflassa_site.littledropsofrain.domain.old.helpers.MainActivityHolder
import app.web.diegoflassa_site.littledropsofrain.domain.old.preferences.MyOnSharedPreferenceChangeListener
import app.web.diegoflassa_site.littledropsofrain.domain.old.services.NewMessagesService
import app.web.diegoflassa_site.littledropsofrain.domain.old.services.SetupProductsUpdateWorkerService
import app.web.diegoflassa_site.littledropsofrain.presentation.old.contracts.EmailLinkAuthActivityResultContract
import app.web.diegoflassa_site.littledropsofrain.presentation.old.ui.MainActivityViewModel
import app.web.diegoflassa_site.littledropsofrain.presentation.old.ui.MainActivityViewState
import app.web.diegoflassa_site.littledropsofrain.presentation.ui.home.HomeIluriaFragment
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
@ExperimentalStdlibApi
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

    private val viewModel: MainActivityViewModel by viewModels()
    private var authenticateOnResume = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchRemoteConfig()
        subscribeToAdminMessages()
        handleIntent()
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, HomeIluriaFragment()).commit()
    }

    private fun fetchRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(
                if (BuildConfig.DEBUG) 0 else TimeUnit.HOURS.toSeconds(12)
            ).build()
        remoteConfig.setConfigSettingsAsync(configSettings).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(
                    TAG,
                    "Config applied successfully"
                )
                remoteConfig.fetchAndActivate()
                    .addOnCompleteListener { taskFetchAndActivate ->
                        if (taskFetchAndActivate.isSuccessful) {
                            Log.d(
                                TAG,
                                "fetchRemoteConfig ${getString(R.string.configuration_fetch_successfull)}"
                            )
                            Toast.makeText(
                                this, getString(R.string.configuration_fetch_successfull),
                                Toast.LENGTH_SHORT
                            ).show()
                            val updated: Boolean? = taskFetchAndActivate.result
                            Log.d(TAG, "Activation successfully. Update value is $updated")
                        } else {
                            taskFetchAndActivate.exception?.printStackTrace()
                            Log.d(
                                TAG,
                                "fetchRemoteConfig ${getString(R.string.configuration_fetch_failed)}. Error was ${taskFetchAndActivate.exception}"
                            )
                            Toast.makeText(
                                this, getString(R.string.configuration_fetch_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Log.d(
                    TAG,
                    "Error applying config"
                )
            }
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

    override fun onResume() {
        super.onResume()
        if (authenticateOnResume) {
            authenticateOnResume = false
            // Navigate to the sign-in/authentication fragment
           // findNavController(R.id.nav_host_fragment).navigate(OldMainActivityDirections.actionGlobalAuthenticationFragment())
        }
        updateUI(viewModel.viewState)
        handleIntent()
    }

    override fun onDestroy() {
        Log.d(TAG, "Main activity destroyed")
        super.onDestroy()
    }

    private fun handleIntent() {
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri: Uri? = intent?.data
        if (uri != null) {
            val scheme: String = uri.scheme!!.lowercase(Locale.ROOT)
            val host: String = uri.host!!.lowercase(Locale.ROOT)
            if ("app" == scheme) {
                //dispatchIntent = mapAppLink(uri)
            } else if (("http" == scheme || "https" == scheme) &&
                ("ldor.page.link" == host || "littledropsofrain-site.web.app" == host || "littledropsofrain.web.app" == host || "littledropsofrain" == host)
            ) {
                when (uri.path) {
                    "/privacy" -> {
                        //findNavController(R.id.nav_host_fragment).navigate(NavMainDirections.actionGlobalPrivacyFragment())
                    }
                    "/tos" -> {
                        //findNavController(R.id.nav_host_fragment).navigate(NavMainDirections.actionGlobalTosFragment())
                    }
                    "/licenses" -> {
                        showLicenses()
                    }
                    "/passwordless" -> {
                        runEmailLinkAuth(intent)
                    }
                }
            }
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
                //findNavController(R.id.nav_host_fragment).navigate(NavMainDirections.actionGlobalSettingsFragment())
                ret = true
            }
            R.id.action_licenses -> {
                showLicenses()
                ret = true
            }
            R.id.action_privacy -> {
                //findNavController(R.id.nav_host_fragment).navigate(NavMainDirections.actionGlobalPrivacyFragment())
                ret = true
            }
            R.id.action_tos -> {
                //findNavController(R.id.nav_host_fragment).navigate(NavMainDirections.actionGlobalTosFragment())
                ret = true
            }
        }
        return ret
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
        Log.d(TAG, "User found: $user")
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
                //findNavController(R.id.nav_host_fragment).navigate(
                   // OldMainActivityDirections.actionGlobalUserProfileFragment()
                //)
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
