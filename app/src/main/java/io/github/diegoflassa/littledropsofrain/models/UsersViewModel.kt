package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.ui.topic.SendTopicMessageFragment

class UsersViewModel : ViewModel() {
    private val mViewState = UsersViewState().apply {
        value?.text = "This is ${UsersViewState::class.simpleName} Fragment"
    }
    val viewState = mViewState
}