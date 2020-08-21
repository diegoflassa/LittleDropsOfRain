package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.ui.subscription.SendTopicMessageFragment

class TopicMessageViewModel : ViewModel() {
    private val mViewState = MutableLiveData<TopicMessageViewState>().apply {
        value?.text = "This is ${SendTopicMessageFragment::class.simpleName} Fragment"
    }
    val viewState: LiveData<TopicMessageViewState> = mViewState

}