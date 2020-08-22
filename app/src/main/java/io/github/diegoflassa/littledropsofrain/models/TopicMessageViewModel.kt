package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.ui.topic.SendTopicMessageFragment

class TopicMessageViewModel : ViewModel() {
    private val mViewState = TopicMessageViewState().apply {
        value?.text = "This is ${SendTopicMessageFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}