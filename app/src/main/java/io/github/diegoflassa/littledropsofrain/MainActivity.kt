package io.github.diegoflassa.littledropsofrain

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import io.github.diegoflassa.littledropsofrain.activities.SendMessageActivity
import io.github.diegoflassa.littledropsofrain.auth.AuthActivityResultContract
import io.github.diegoflassa.littledropsofrain.data.dao.UserDao
import io.github.diegoflassa.littledropsofrain.data.entities.User
import io.github.diegoflassa.littledropsofrain.databinding.ActivityMainBinding
import io.github.diegoflassa.littledropsofrain.ui.SettingsActivity
import kotlinx.android.synthetic.main.nav_header_main.view.*


class MainActivity : AppCompatActivity(), ActivityResultCallback<Int> {

    private lateinit var appBarConfiguration: AppBarConfiguration
    companion object{
        const val TAG ="MainActivity"
    }

    private lateinit var mAuth : FirebaseAuth
    private lateinit var fab: FloatingActionButton
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        fab = findViewById(R.id.fab)
        fab.isEnabled= false
        fab.setOnClickListener {
            startActivity(Intent(this, SendMessageActivity::class.java))
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
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
                if( currentUser != null ){
                    Picasso.get().load(currentUser.photoUrl).placeholder(R.drawable.image_placeholder).into( binding.navView.imageView)
                }else{
                    //Picasso.get().load(R.mipmap.ic_launcher_round).placeholder(R.drawable.image_placeholder).into( binding.navView.imageView)
                    binding.navView.imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_launcher_round))
                }
            }
        })

        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_iluria, R.id.nav_home, R.id.nav_facebook, R.id.nav_admin), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser==null){
            // Create and launch sign-in intent
            registerForActivityResult(AuthActivityResultContract(), this ).launch(null)
        }else{
            Toast.makeText(applicationContext, getString(R.string.user_logged_as, mAuth.currentUser!!.displayName), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val menuItem = menu.findItem(R.id.action_authentication)
        val mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser==null){
            menuItem.title = getString(R.string.login)
            fab.isEnabled= false
        }else{
            menuItem.title = getString(R.string.logout)
            fab.isEnabled= true
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var ret= false
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                ret= true
            }
            R.id.action_authentication -> {
                fab.isEnabled= false
                if(mAuth.currentUser==null){
                    // Create and launch sign-in intent
                    registerForActivityResult(AuthActivityResultContract(), this ).launch(null)
                    item.title = getString(R.string.logout)
                }else{
                    logout()
                    item.title = getString(R.string.login)
                }
            }
        }
        return ret
    }

    override fun onActivityResult(result : Int) {
        if (result == Activity.RESULT_OK) {
            fab.isEnabled= true
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            if(user!=null) {
                val userFb = User()
                userFb.uid = user.uid
                userFb.name = user.displayName
                userFb.email = user.email
                UserDao.insert(userFb)
            }
            Toast.makeText(this, getString(R.string.log_in_successful), Toast.LENGTH_SHORT).show()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(this, R.string.unable_to_log_in, Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout(){
        AuthUI.getInstance().signOut(this)
    }

}