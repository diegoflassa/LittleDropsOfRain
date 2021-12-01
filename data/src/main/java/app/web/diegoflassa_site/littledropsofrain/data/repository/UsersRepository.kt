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

package app.web.diegoflassa_site.littledropsofrain.data.repository

import android.util.Log
import app.web.diegoflassa_site.littledropsofrain.data.dao.UsersDao
import app.web.diegoflassa_site.littledropsofrain.data.entities.User
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataFailureListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnUserFoundListener
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnUsersLoadedListener

@SuppressWarnings("deprecation", "removal")
class UsersRepository(private val usersDao: UsersDao) {

    private val tag: String? = UsersRepository::class.simpleName

    fun loadAll(listener: OnUsersLoadedListener) {
        Log.i(tag, "loadAll")
        usersDao.loadAll(listener)
    }

    fun loadAllByIds(
        userIds: List<String>,
        listener: OnUsersLoadedListener
    ) {
        Log.i(tag, "loadAllByIds")
        usersDao.loadAllByIds(userIds, listener)
    }

    fun findByName(name: String?, listener: OnUsersLoadedListener) {
        Log.i(tag, "findByName")
        usersDao.findByName(name, listener)
    }

    fun findByEMail(email: String?, foundListener: OnUserFoundListener) {
        Log.i(tag, "findByEMail")
        usersDao.findByEMail(email, foundListener)
    }

    fun insertAll(
        users: List<User>,
        onSuccessListener: OnDataChangeListener<Void?>,
        onFailureListener: OnDataFailureListener<Exception>
    ) {
        Log.i(tag, "insertAll")
        usersDao.insertAll(users, onSuccessListener, onFailureListener)
    }

    fun upsert(
        user: User,
        onSuccessListener: OnDataChangeListener<Void?>? = null,
        onFailureListener: OnDataFailureListener<Exception>? = null
    ) {
        Log.i(tag, "upsert")
        usersDao.upsert(user, onSuccessListener, onFailureListener)
    }

    fun delete(user: User?) {
        Log.i(tag, "delete")
        usersDao.delete(user)
    }

    fun deleteAll() {
        Log.i(tag, "deleteAll")
        usersDao.deleteAll()
    }
}
