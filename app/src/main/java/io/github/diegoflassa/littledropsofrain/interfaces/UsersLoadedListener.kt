package io.github.diegoflassa.littledropsofrain.interfaces

import io.github.diegoflassa.littledropsofrain.data.entities.User

interface UsersLoadedListener {
    fun onUsersLoaded(users: List<User>)
}