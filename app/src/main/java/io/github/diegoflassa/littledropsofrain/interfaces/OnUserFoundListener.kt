package io.github.diegoflassa.littledropsofrain.interfaces

import io.github.diegoflassa.littledropsofrain.data.entities.User

interface OnUserFoundListener {
    fun onUserFound(user: User?)
}