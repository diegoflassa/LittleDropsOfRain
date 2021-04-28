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

package app.web.diegoflassa_site.littledropsofrain.helpers

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import app.web.diegoflassa_site.littledropsofrain.auth.FirebaseAuthLiveData
import app.web.diegoflassa_site.littledropsofrain.auth.UserLiveData
import app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.interfaces.OnUserFoundListener

object LoggedUser : OnUserFoundListener, LifecycleOwner {

    private var lifecycleRegistry = LifecycleRegistry(this)
    private val firebaseUserLiveData = FirebaseAuthLiveData()
    var userLiveData = UserLiveData()

    init {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        firebaseUserLiveData.observe(
            this,
            { firebaseUser ->
                if (firebaseUser != null) {
                    UserDao.findByEMail(firebaseUser.email, this)
                }
            }
        )
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onUserFound(user: User?) {
        userLiveData.value = user
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}
