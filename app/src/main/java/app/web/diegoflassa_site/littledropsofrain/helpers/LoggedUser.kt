package app.web.diegoflassa_site.littledropsofrain.helpers

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import app.web.diegoflassa_site.littledropsofrain.auth.FirebaseAuthLiveData
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnUserFoundListener

object LoggedUser : OnUserFoundListener, LifecycleOwner {

    private var lifecycleRegistry = LifecycleRegistry(this)
    val firebaseUserLiveData = FirebaseAuthLiveData()
    var user: User? = null

    init {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        firebaseUserLiveData.observe(this, { firebaseUser ->
            if (firebaseUser != null) {
                UserDao.findByEMail(firebaseUser.email, this)
            } else {
                user = firebaseUser
            }
        })
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onUserFound(user: User?) {
        LoggedUser.user = user
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}