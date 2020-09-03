package app.web.diegoflassa_site.littledropsofrain.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.data.model.User
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import app.web.diegoflassa_site.littledropsofrain.R

class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme_Launcher)
        super.onCreate(savedInstanceState)

        val user : User?= null// = UserDb.getCurrentUser()
        routeToAppropriatePage(user)
        finish()
    }

    private fun routeToAppropriatePage(user: User?) {
        // Example routing
        when (user) {
            null -> startActivity(Intent(this, MainActivity::class.java))
        }
    }
}