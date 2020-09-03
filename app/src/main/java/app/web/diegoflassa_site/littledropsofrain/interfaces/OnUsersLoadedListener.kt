package app.web.diegoflassa_site.littledropsofrain.interfaces

import app.web.diegoflassa_site.littledropsofrain.data.entities.User

interface OnUsersLoadedListener {
    fun onUsersLoaded(users: List<User>)
}